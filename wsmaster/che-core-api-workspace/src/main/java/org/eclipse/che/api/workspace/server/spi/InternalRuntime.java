/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.workspace.server.spi;

import static java.util.stream.Collectors.toMap;
import static org.eclipse.che.api.core.model.workspace.config.ServerConfig.INTERNAL_SERVER_ATTRIBUTE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.che.api.core.model.workspace.Warning;
import org.eclipse.che.api.core.model.workspace.WorkspaceStatus;
import org.eclipse.che.api.core.model.workspace.runtime.Machine;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.core.model.workspace.runtime.Server;
import org.eclipse.che.api.workspace.server.URLRewriter;
import org.eclipse.che.api.workspace.server.model.impl.MachineImpl;
import org.eclipse.che.api.workspace.server.model.impl.ServerImpl;
import org.eclipse.che.api.workspace.server.model.impl.WarningImpl;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Implementation of concrete Runtime.
 *
 * @author gazarenkov
 */
public abstract class InternalRuntime<T extends RuntimeContext> {

  private final T context;
  private final URLRewriter urlRewriter;
  private final List<Warning> warnings;
  protected WorkspaceStatus status;

  /**
   * @param context prepared context for runtime starting
   * @param urlRewriter url rewriter
   * @param warnings warning that occurred while context preparing
   */
  public InternalRuntime(T context, URLRewriter urlRewriter, List<Warning> warnings) {
    this.context = context;
    this.urlRewriter = urlRewriter;
    this.warnings = new CopyOnWriteArrayList<>();
    if (warnings != null) {
      this.warnings.addAll(warnings);
    }
  }

  /**
   * @param context prepared context for runtime starting
   * @param urlRewriter url rewriter
   * @param warnings warning that occurred while context preparing
   * @param status status of the runtime, or null if {@link #start(Map)} and {@link #stop(Map)} were
   *     not invoked yet
   */
  public InternalRuntime(
      T context,
      URLRewriter urlRewriter,
      List<Warning> warnings,
      @Nullable WorkspaceStatus status) {
    this.context = context;
    this.urlRewriter = urlRewriter;
    this.warnings = new CopyOnWriteArrayList<>();
    if (warnings != null) {
      this.warnings.addAll(warnings);
    }
    this.status = status;
  }

  /** Returns name of the active environment. */
  public String getActiveEnv() {
    return context.getIdentity().getEnvName();
  }

  /** Returns identifier of user who started a runtime. */
  public String getOwner() {
    return context.getIdentity().getOwnerId();
  }

  /** Returns identifier of user who started a runtime. */
  public List<? extends Warning> getWarnings() {
    return warnings;
  }

  /**
   * Returns map of machine name to machine instance entries.
   *
   * @throws InfrastructureException when any error occurs
   */
  public Map<String, ? extends Machine> getMachines() throws InfrastructureException {
    return getInternalMachines()
        .entrySet()
        .stream()
        .collect(
            toMap(
                Map.Entry::getKey,
                e ->
                    new MachineImpl(
                        e.getValue().getAttributes(),
                        rewriteExternalServers(e.getKey(), e.getValue().getServers()),
                        e.getValue().getStatus())));
  }

  /**
   * Returns map of machine name to machine instance entries.
   *
   * <p>Implementation must not return null
   *
   * @throws InfrastructureException when any error occurs
   */
  protected abstract Map<String, ? extends Machine> getInternalMachines()
      throws InfrastructureException;

  /**
   * Returns runtime status.
   *
   * @throws InfrastructureException when any error occurs
   */
  public WorkspaceStatus getStatus() throws InfrastructureException {
    return status == null ? WorkspaceStatus.STOPPED : status;
  }

  /**
   * Starts Runtime. In practice this method launching supposed to take unpredictable long time so
   * normally it should be launched in separated thread
   *
   * @param startOptions optional parameters
   * @throws StateException when the context is already used
   * @throws InternalInfrastructureException when error that indicates system internal problem
   *     occurs
   * @throws RuntimeStartInterruptedException when start execution is cancelled
   * @throws InfrastructureException when any other error occurs
   */
  public void start(Map<String, String> startOptions) throws InfrastructureException {
    markStarting();
    try {
      internalStart(startOptions);
      markRunning();
    } catch (InfrastructureException e) {
      markStopped();
      throw e;
    }
  }

  /**
   * Starts underlying environment in implementation specific way.
   *
   * @param startOptions options of workspace that may be used in environment start
   * @throws InternalInfrastructureException when error that indicates system internal problem
   *     occurs
   * @throws RuntimeStartInterruptedException when start execution is cancelled
   * @throws InfrastructureException when any other error occurs
   */
  protected abstract void internalStart(Map<String, String> startOptions)
      throws InfrastructureException;

  /**
   * Stops Runtime. Presumably can take some time so considered to launch in separate thread.
   *
   * <p>Runtime will be stopped only if its state {@link WorkspaceStatus#RUNNING} or {@link
   * WorkspaceStatus#STARTING}.
   *
   * @param stopOptions options of workspace that may used in environment stop
   * @throws StateException when the runtime can't be stopped because otherwise it would be in
   *     inconsistent status (e.g. stop(interrupt) might not be allowed during start)
   * @throws InfrastructureException when any other error occurs
   */
  public final void stop(Map<String, String> stopOptions) throws InfrastructureException {
    markStopping();
    try {
      internalStop(stopOptions);
    } finally {
      markStopped();
    }
  }

  /**
   * Stops Runtime in an implementation specific way.
   *
   * <ul>
   *   <li>When runtime state is {@link WorkspaceStatus#STARTING} then process of start must be
   *       cancelled and all the resources must be released including the cases when an exception
   *       occurs.
   *   <li>When runtime state is {@link WorkspaceStatus#RUNNING} then runtime must be normally
   *       stopped and all the resources must be released including the cases when an exception
   *       occurs.
   * </ul>
   *
   * @param stopOptions workspace options that may be used on runtime stop
   * @throws InfrastructureException when any other error occurs
   */
  protected abstract void internalStop(Map<String, String> stopOptions)
      throws InfrastructureException;

  /** @return some implementation specific properties if any */
  public abstract Map<String, String> getProperties();

  /** @return the Context */
  public final T getContext() {
    return context;
  }

  /**
   * Convenient method to rewrite incoming external servers in a loop
   *
   * @param incoming servers
   * @return rewritten Map of Servers (name -> Server)
   */
  private Map<String, Server> rewriteExternalServers(
      String machineName, Map<String, ? extends Server> incoming) {
    Map<String, Server> outgoing = new HashMap<>();
    RuntimeIdentity identity = context.getIdentity();
    for (Map.Entry<String, ? extends Server> entry : incoming.entrySet()) {
      String name = entry.getKey();
      Server incomingServer = entry.getValue();
      if (Boolean.parseBoolean(incomingServer.getAttributes().get(INTERNAL_SERVER_ATTRIBUTE))) {
        outgoing.put(name, incomingServer);
      } else {
        try {
          ServerImpl server =
              new ServerImpl(incomingServer)
                  .withUrl(
                      urlRewriter.rewriteURL(identity, machineName, name, incomingServer.getUrl()));
          outgoing.put(name, server);
        } catch (InfrastructureException e) {
          warnings.add(new WarningImpl(101, "Malformed URL for " + name + " : " + e.getMessage()));
        }
      }
    }

    return outgoing;
  }

  /**
   * Marks runtime as {@link WorkspaceStatus#STARTING STARTING}.
   *
   * <p>Note that this method must be overridden if runtime implementation stores status itself.
   *
   * @throws StateException when the runtime was already marked as STARTING
   * @throws StateException when the runtime was already marked as STARTING
   * @throws InfrastructureException when any other exception occurs
   */
  protected void markStarting() throws InfrastructureException {
    if (status != null) {
      throw new StateException("Runtime already started");
    }
    this.status = WorkspaceStatus.STARTING;
  }

  /**
   * Marks runtime as {@link WorkspaceStatus#RUNNING RUNNING}.
   *
   * <p>Note that this method must be overridden if runtime implementation stores status itself.
   *
   * @throws InfrastructureException when any exception occurs
   */
  protected void markRunning() throws InfrastructureException {
    this.status = WorkspaceStatus.RUNNING;
  }

  /**
   * Marks runtime as {@link WorkspaceStatus#STOPPING STOPPING}.
   *
   * <p>Note that this method must be overridden if runtime implementation stores status itself.
   * Also this method must be overridden if runtime implementation doesn't support start
   * interruption.
   *
   * @throws StateException when the runtime is not RUNNING or STARTING
   * @throws StateException when the runtime is STARTING and implementation doesn't support start
   *     interruption
   * @throws InfrastructureException when any exception occurs
   */
  protected void markStopping() throws InfrastructureException {
    if (status != WorkspaceStatus.RUNNING && status != WorkspaceStatus.STARTING) {
      throw new StateException("The environment must be running or starting");
    }
    status = WorkspaceStatus.STOPPING;
  }

  /**
   * Marks runtime as {@link WorkspaceStatus#STOPPED STOPPED}.
   *
   * <p>Note that this method must be overridden if runtime implementation stores status itself.
   *
   * @throws InfrastructureException when any exception occurs
   */
  protected void markStopped() throws InfrastructureException {
    status = WorkspaceStatus.STOPPED;
  }
}
