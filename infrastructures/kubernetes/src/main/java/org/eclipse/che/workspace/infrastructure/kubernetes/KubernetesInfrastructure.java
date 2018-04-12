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
package org.eclipse.che.workspace.infrastructure.kubernetes;

import static java.lang.String.format;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.ValidationException;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.api.workspace.server.spi.InternalInfrastructureException;
import org.eclipse.che.api.workspace.server.spi.RuntimeInfrastructure;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironment;
import org.eclipse.che.api.workspace.server.spi.provision.InternalEnvironmentProvisioner;
import org.eclipse.che.workspace.infrastructure.docker.environment.dockerimage.DockerImageEnvironment;
import org.eclipse.che.workspace.infrastructure.kubernetes.environment.KubernetesEnvironment;
import org.eclipse.che.workspace.infrastructure.kubernetes.environment.convert.DockerImageEnvironmentConverter;

/** @author Sergii Leshchenko */
@Singleton
public class KubernetesInfrastructure extends RuntimeInfrastructure {

  public static final String NAME = "kubernetes";

  private final DockerImageEnvironmentConverter dockerImageEnvConverter;
  private final KubernetesRuntimeContextFactory runtimeContextFactory;
  private final KubernetesEnvironmentProvisioner k8sEnvProvisioner;

  @Inject
  public KubernetesInfrastructure(
      EventService eventService,
      KubernetesRuntimeContextFactory runtimeContextFactory,
      KubernetesEnvironmentProvisioner k8sEnvProvisioner,
      Set<InternalEnvironmentProvisioner> internalEnvProvisioners,
      DockerImageEnvironmentConverter dockerImageEnvConverter) {
    super(
        NAME,
        ImmutableSet.of(KubernetesEnvironment.TYPE, DockerImageEnvironment.TYPE),
        eventService,
        internalEnvProvisioners);
    this.runtimeContextFactory = runtimeContextFactory;
    this.k8sEnvProvisioner = k8sEnvProvisioner;
    this.dockerImageEnvConverter = dockerImageEnvConverter;
  }

  @Override
  protected KubernetesRuntimeContext internalPrepare(
      RuntimeIdentity id, InternalEnvironment environment)
      throws ValidationException, InfrastructureException {
    final KubernetesEnvironment kubernetesEnvironment = asKubernetesEnv(environment);

    k8sEnvProvisioner.provision(kubernetesEnvironment, id);

    return runtimeContextFactory.create(kubernetesEnvironment, id, this);
  }

  private KubernetesEnvironment asKubernetesEnv(InternalEnvironment source)
      throws ValidationException, InfrastructureException {
    if (source instanceof KubernetesEnvironment) {
      return (KubernetesEnvironment) source;
    }
    if (source instanceof DockerImageEnvironment) {
      return dockerImageEnvConverter.convert((DockerImageEnvironment) source);
    }
    throw new InternalInfrastructureException(
        format(
            "Environment type '%s' is not supported. Supported environment types: %s",
            source.getRecipe().getType(), KubernetesEnvironment.TYPE));
  }
}
