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
package org.eclipse.che.workspace.infrastructure.docker.environment.convert;

import static org.eclipse.che.workspace.infrastructure.docker.ArgumentsValidator.checkArgument;

import com.google.common.base.Joiner;
import org.eclipse.che.api.core.ValidationException;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.environment.dockerimage.DockerImageEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.model.DockerContainerConfig;
import org.eclipse.che.workspace.infrastructure.docker.model.DockerEnvironment;

/**
 * Converts {@link DockerImageEnvironment} to {@link DockerEnvironment}.
 *
 * @author Alexander Garagatyi
 * @author Alexander Andrienko
 */
public class DockerImageEnvironmentConverter implements DockerEnvironmentConverter {

  @Override
  public DockerEnvironment convert(InternalEnvironment environment) throws ValidationException {
    if (!(environment instanceof DockerImageEnvironment)) {
      throw new ValidationException("The specified environment is not docker image environment");
    }

    DockerImageEnvironment dockerImageEnv = (DockerImageEnvironment) environment;
    String machineName = getMachineName(dockerImageEnv);

    DockerContainerConfig container =
        new DockerContainerConfig().setImage(dockerImageEnv.getDockerImage());

    DockerEnvironment dockerEnv =
        new DockerEnvironment(
            environment.getRecipe(), environment.getMachines(), environment.getWarnings());
    dockerEnv.getContainers().put(machineName, container);
    return dockerEnv;
  }

  private String getMachineName(InternalEnvironment environment) throws ValidationException {
    checkArgument(
        environment.getMachines().size() == 1,
        "Environment of type '%s' doesn't support multiple machines, but contains machines: %s",
        environment.getRecipe().getType(),
        Joiner.on(", ").join(environment.getMachines().keySet()));

    return environment.getMachines().keySet().iterator().next();
  }
}
