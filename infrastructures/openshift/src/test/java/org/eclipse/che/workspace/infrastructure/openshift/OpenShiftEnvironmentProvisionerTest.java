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
package org.eclipse.che.workspace.infrastructure.openshift;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;

import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.pvc.WorkspaceVolumesStrategy;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.InstallerServersPortProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.LogsVolumeMachineProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.PodTerminationGracePeriodProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.env.EnvVarsConverter;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.limits.ram.RamLimitProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.restartpolicy.RestartPolicyRewriter;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.server.ServersConverter;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;
import org.eclipse.che.workspace.infrastructure.openshift.provision.OpenShiftUniqueNamesProvisioner;
import org.eclipse.che.workspace.infrastructure.openshift.provision.RouteTlsProvisioner;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests {@link OpenShiftEnvironmentProvisioner}.
 *
 * @author Anton Korneta
 */
@Listeners(MockitoTestNGListener.class)
public class OpenShiftEnvironmentProvisionerTest {

  @Mock private WorkspaceVolumesStrategy volumesStrategy;
  @Mock private InstallerServersPortProvisioner installerServersPortProvisioner;
  @Mock private OpenShiftUniqueNamesProvisioner uniqueNamesProvisioner;
  @Mock private OpenShiftEnvironment osEnv;
  @Mock private RuntimeIdentity runtimeIdentity;
  @Mock private RouteTlsProvisioner tlsRouteProvisioner;
  @Mock private EnvVarsConverter envVarsProvisioner;
  @Mock private ServersConverter serversProvisioner;
  @Mock private RestartPolicyRewriter restartPolicyRewriter;
  @Mock private RamLimitProvisioner ramLimitProvisioner;
  @Mock private LogsVolumeMachineProvisioner logsVolumeMachineProvisioner;
  @Mock private PodTerminationGracePeriodProvisioner podTerminationGracePeriodProvisioner;

  private OpenShiftEnvironmentProvisioner osInfraProvisioner;

  private InOrder provisionOrder;

  @BeforeMethod
  public void setUp() {
    osInfraProvisioner =
        new OpenShiftEnvironmentProvisioner(
            true,
            uniqueNamesProvisioner,
            tlsRouteProvisioner,
            serversProvisioner,
            envVarsProvisioner,
            restartPolicyRewriter,
            volumesStrategy,
            ramLimitProvisioner,
            installerServersPortProvisioner,
            logsVolumeMachineProvisioner,
            podTerminationGracePeriodProvisioner);
    provisionOrder =
        inOrder(
            installerServersPortProvisioner,
            logsVolumeMachineProvisioner,
            serversProvisioner,
            envVarsProvisioner,
            volumesStrategy,
            uniqueNamesProvisioner,
            tlsRouteProvisioner,
            restartPolicyRewriter,
            ramLimitProvisioner,
            podTerminationGracePeriodProvisioner);
  }

  @Test
  public void performsOrderedProvisioning() throws Exception {
    osInfraProvisioner.provision(osEnv, runtimeIdentity);

    provisionOrder
        .verify(installerServersPortProvisioner)
        .provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(logsVolumeMachineProvisioner).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(serversProvisioner).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(envVarsProvisioner).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(volumesStrategy).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(restartPolicyRewriter).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(uniqueNamesProvisioner).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(tlsRouteProvisioner).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verify(ramLimitProvisioner).provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder
        .verify(podTerminationGracePeriodProvisioner)
        .provision(eq(osEnv), eq(runtimeIdentity));
    provisionOrder.verifyNoMoreInteractions();
  }
}
