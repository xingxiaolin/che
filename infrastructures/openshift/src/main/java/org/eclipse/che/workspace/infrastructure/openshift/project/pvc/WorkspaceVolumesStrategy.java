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
package org.eclipse.che.workspace.infrastructure.openshift.project.pvc;

import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;
import org.eclipse.che.workspace.infrastructure.openshift.provision.ConfigurationProvisioner;

/**
 * Defines a basic set of operations for workspace volume provisioning strategies.
 *
 * @author Anton Korneta
 */
public interface WorkspaceVolumesStrategy extends ConfigurationProvisioner {

  /**
   * Configures the workspace PVCs, volumes, claim bindings with a strategy specific options.
   *
   * @param osEnv OpenShift environment
   * @param identity runtime identity
   * @throws InfrastructureException when any error occurs while provisioning volumes
   */
  @Override
  void provision(OpenShiftEnvironment osEnv, RuntimeIdentity identity)
      throws InfrastructureException;

  /**
   * Prepares volumes for backup of workspace data on a specific machine in a strategy specific way.
   * Note that this step, depending on the strategy, may take some time.
   *
   * @param osEnv OpenShift environment that changes as a result of preparation
   * @param workspaceId the workspace identifier for which volumes will be prepared
   * @throws InfrastructureException when any error while preparation occurs
   */
  void prepare(OpenShiftEnvironment osEnv, String workspaceId) throws InfrastructureException;

  /**
   * Cleanups workspace backed up data in a strategy specific way.
   *
   * @param workspaceId the workspace identifier for which cleanup will be performed
   * @throws InfrastructureException when any error while cleanup occurs
   */
  void cleanup(String workspaceId) throws InfrastructureException;
}
