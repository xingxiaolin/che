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
package org.eclipse.che.workspace.infrastructure.kubernetes.environment;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.che.api.core.model.workspace.Warning;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironment;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.api.workspace.server.spi.environment.InternalRecipe;

/**
 * Holds objects of Kubernetes environment.
 *
 * @author Sergii Leshchenko
 */
public class KubernetesEnvironment extends InternalEnvironment {

  public static final String TYPE = "kubernetes";

  private final Map<String, Pod> pods;
  private final Map<String, Service> services;
  private final Map<String, Ingress> ingresses;
  private final Map<String, PersistentVolumeClaim> persistentVolumeClaims;

  public KubernetesEnvironment(KubernetesEnvironment k8sEnv) {
    this(
        k8sEnv.getRecipe(),
        k8sEnv.getMachines(),
        k8sEnv.getWarnings(),
        k8sEnv.getPods(),
        k8sEnv.getServices(),
        k8sEnv.getIngresses(),
        k8sEnv.getPersistentVolumeClaims());
  }

  public static Builder builder() {
    return new Builder();
  }

  protected KubernetesEnvironment(
      InternalRecipe internalRecipe,
      Map<String, InternalMachineConfig> machines,
      List<Warning> warnings,
      Map<String, Pod> pods,
      Map<String, Service> services,
      Map<String, Ingress> ingresses,
      Map<String, PersistentVolumeClaim> persistentVolumeClaims) {
    super(internalRecipe, machines, warnings);
    this.pods = pods;
    this.services = services;
    this.ingresses = ingresses;
    this.persistentVolumeClaims = persistentVolumeClaims;
  }

  /** Returns pods that should be created when environment starts. */
  public Map<String, Pod> getPods() {
    return pods;
  }

  /** Returns services that should be created when environment starts. */
  public Map<String, Service> getServices() {
    return services;
  }

  /** Returns ingresses that should be created when environment starts. */
  public Map<String, Ingress> getIngresses() {
    return ingresses;
  }

  /** Returns PVCs that should be created when environment starts. */
  public Map<String, PersistentVolumeClaim> getPersistentVolumeClaims() {
    return persistentVolumeClaims;
  }

  public static class Builder {
    protected InternalRecipe internalRecipe;
    protected final Map<String, InternalMachineConfig> machines = new HashMap<>();
    protected final List<Warning> warnings = new ArrayList<>();
    protected final Map<String, Pod> pods = new HashMap<>();
    protected final Map<String, Service> services = new HashMap<>();
    protected final Map<String, Ingress> ingresses = new HashMap<>();
    protected final Map<String, PersistentVolumeClaim> persistentVolumeClaims = new HashMap<>();

    protected Builder() {}

    public Builder setInternalRecipe(InternalRecipe internalRecipe) {
      this.internalRecipe = internalRecipe;
      return this;
    }

    public Builder setMachines(Map<String, InternalMachineConfig> machines) {
      this.machines.putAll(machines);
      return this;
    }

    public Builder setWarnings(List<Warning> warnings) {
      this.warnings.addAll(warnings);
      return this;
    }

    public Builder setPods(Map<String, Pod> pods) {
      this.pods.putAll(pods);
      return this;
    }

    public Builder setServices(Map<String, Service> services) {
      this.services.putAll(services);
      return this;
    }

    public Builder setIngresses(Map<String, Ingress> ingresses) {
      this.ingresses.putAll(ingresses);
      return this;
    }

    public Builder setPersistentVolumeClaims(Map<String, PersistentVolumeClaim> pvcs) {
      this.persistentVolumeClaims.putAll(pvcs);
      return this;
    }

    public KubernetesEnvironment build() {
      return new KubernetesEnvironment(
          internalRecipe, machines, warnings, pods, services, ingresses, persistentVolumeClaims);
    }
  }
}
