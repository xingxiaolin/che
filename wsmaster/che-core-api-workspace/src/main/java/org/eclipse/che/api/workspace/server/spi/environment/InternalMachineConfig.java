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
package org.eclipse.che.api.workspace.server.spi.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.che.api.core.model.workspace.config.ServerConfig;
import org.eclipse.che.api.core.model.workspace.config.Volume;
import org.eclipse.che.api.installer.shared.model.Installer;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;

/**
 * Machine Config to use inside infrastructure.
 *
 * <p>It contains:
 *
 * <ul>
 *   <li>retrieved full information about installers;
 *   <li>normalized server configs.
 * </ul>
 *
 * @author gazarenkov
 */
public class InternalMachineConfig {
  private final List<Installer> installers;
  private final Map<String, ServerConfig> servers;
  private final Map<String, String> env;
  private final Map<String, String> attributes;
  private final Map<String, Volume> volumes;

  InternalMachineConfig(
      List<Installer> installers,
      Map<String, ? extends ServerConfig> servers,
      Map<String, String> env,
      Map<String, String> attributes,
      Map<String, ? extends Volume> volumes)
      throws InfrastructureException {
    this.servers = new HashMap<>();
    if (servers != null) {
      this.servers.putAll(servers);
    }

    this.installers = new ArrayList<>();
    if (installers != null) {
      this.installers.addAll(installers);
    }

    this.env = new HashMap<>();
    if (env != null) {
      this.env.putAll(env);
    }

    this.attributes = new HashMap<>();
    if (attributes != null) {
      this.attributes.putAll(attributes);
    }

    this.volumes = new HashMap<>();
    if (volumes != null) {
      this.volumes.putAll(volumes);
    }
  }

  /** Returns unmodifiable ordered list of installers configs of the machine. */
  public List<Installer> getInstallers() {
    return Collections.unmodifiableList(installers);
  }

  /** Returns modifiable map of servers configured in the machine. */
  public Map<String, ServerConfig> getServers() {
    return servers;
  }

  /** Returns modifiable map of machine environment variables. */
  public Map<String, String> getEnv() {
    return env;
  }

  /** Returns modifiable map of machine attributes. */
  public Map<String, String> getAttributes() {
    return attributes;
  }

  /** Returns modifiable map of machine volumes. */
  public Map<String, Volume> getVolumes() {
    return volumes;
  }
}
