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
package org.eclipse.che.workspace.infrastructure.docker.provisioner;

import java.util.Set;
import javax.inject.Inject;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.workspace.infrastructure.docker.model.DockerEnvironment;

/**
 * Applies {@link ContainerSystemSettingsProvisioner}s to docker environment. Provisioners order is
 * not respected, so no provisioner is allowed to be dependent on any other provisioner.
 *
 * @author Alexander Garagatyi
 */
public class ContainerSystemSettingsProvisionersApplier implements ConfigurationProvisioner {
  private Set<ContainerSystemSettingsProvisioner> dockerSettingsProvisioners;

  @Inject
  public ContainerSystemSettingsProvisionersApplier(
      Set<ContainerSystemSettingsProvisioner> dockerSettingsProvisioners) {
    this.dockerSettingsProvisioners = dockerSettingsProvisioners;
  }

  @Override
  public void provision(DockerEnvironment internalEnv, RuntimeIdentity identity)
      throws InfrastructureException {
    for (ContainerSystemSettingsProvisioner dockerSettingsProvisioner :
        dockerSettingsProvisioners) {
      dockerSettingsProvisioner.provision(internalEnv);
    }
  }
}
