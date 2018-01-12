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
package org.eclipse.che.workspace.infrastructure.openshift.provision;

import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.workspace.infrastructure.openshift.OpenShiftInfrastructure;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;

/**
 * Modifies workspace environment configuration and OpenShift environment with everything needed for
 * some logical part of {@link OpenShiftInfrastructure}.
 *
 * @author Anton Korneta
 */
public interface ConfigurationProvisioner {

  /**
   * Configures the OpenShift environment and workspace environment with infrastructure needs.
   *
   * @param osEnv OpenShift environment
   * @param identity runtime identity
   * @throws InfrastructureException when any error occurs
   */
  void provision(OpenShiftEnvironment osEnv, RuntimeIdentity identity)
      throws InfrastructureException;
}
