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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;

import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.workspace.infrastructure.kubernetes.environment.KubernetesEnvironment;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.pvc.WorkspaceVolumesStrategy;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.IngressTlsProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.InstallerServersPortProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.LogsVolumeMachineProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.PodTerminationGracePeriodProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.SecurityContextProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.UniqueNamesProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.env.EnvVarsConverter;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.limits.ram.RamLimitProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.restartpolicy.RestartPolicyRewriter;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.server.ServersConverter;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests {@link KubernetesEnvironmentProvisioner}.
 *
 * @author Anton Korneta
 */
@Listeners(MockitoTestNGListener.class)
public class KubernetesEnvironmentProvisionerTest {

  @Mock private WorkspaceVolumesStrategy volumesStrategy;
  @Mock private InstallerServersPortProvisioner installerServersPortProvisioner;
  @Mock private UniqueNamesProvisioner<KubernetesEnvironment> uniqueNamesProvisioner;
  @Mock private KubernetesEnvironment k8sEnv;
  @Mock private RuntimeIdentity runtimeIdentity;
  @Mock private EnvVarsConverter envVarsProvisioner;
  @Mock private ServersConverter serversProvisioner;
  @Mock private RestartPolicyRewriter restartPolicyRewriter;
  @Mock private RamLimitProvisioner ramLimitProvisioner;
  @Mock private LogsVolumeMachineProvisioner logsVolumeMachineProvisioner;
  @Mock private SecurityContextProvisioner securityContextProvisioner;
  @Mock private PodTerminationGracePeriodProvisioner podTerminationGracePeriodProvisioner;
  @Mock private IngressTlsProvisioner externalServerIngressTlsProvisioner;

  private KubernetesEnvironmentProvisioner osInfraProvisioner;

  private InOrder provisionOrder;

  @BeforeMethod
  public void setUp() {
    osInfraProvisioner =
        new KubernetesEnvironmentProvisioner(
            true,
            uniqueNamesProvisioner,
            serversProvisioner,
            envVarsProvisioner,
            restartPolicyRewriter,
            volumesStrategy,
            ramLimitProvisioner,
            installerServersPortProvisioner,
            logsVolumeMachineProvisioner,
            securityContextProvisioner,
            podTerminationGracePeriodProvisioner,
            externalServerIngressTlsProvisioner);
    provisionOrder =
        inOrder(
            installerServersPortProvisioner,
            logsVolumeMachineProvisioner,
            volumesStrategy,
            uniqueNamesProvisioner,
            serversProvisioner,
            envVarsProvisioner,
            restartPolicyRewriter,
            ramLimitProvisioner,
            securityContextProvisioner,
            podTerminationGracePeriodProvisioner,
            externalServerIngressTlsProvisioner);
  }

  @Test
  public void performsOrderedProvisioning() throws Exception {
    osInfraProvisioner.provision(k8sEnv, runtimeIdentity);

    provisionOrder
        .verify(installerServersPortProvisioner)
        .provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(logsVolumeMachineProvisioner).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(serversProvisioner).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(envVarsProvisioner).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(volumesStrategy).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(restartPolicyRewriter).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(uniqueNamesProvisioner).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(ramLimitProvisioner).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder
        .verify(externalServerIngressTlsProvisioner)
        .provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verify(securityContextProvisioner).provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder
        .verify(podTerminationGracePeriodProvisioner)
        .provision(eq(k8sEnv), eq(runtimeIdentity));
    provisionOrder.verifyNoMoreInteractions();
  }
}
