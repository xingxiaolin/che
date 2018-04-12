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
package org.eclipse.che.workspace.infrastructure.docker.monit;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.workspace.server.WorkspaceManager;
import org.eclipse.che.api.workspace.server.WorkspaceRuntimes;
import org.eclipse.che.api.workspace.server.model.impl.MachineImpl;
import org.eclipse.che.api.workspace.server.model.impl.RuntimeImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.eclipse.che.infrastructure.docker.client.DockerConnector;
import org.eclipse.che.infrastructure.docker.client.json.ContainerListEntry;
import org.eclipse.che.infrastructure.docker.client.json.network.ContainerInNetwork;
import org.eclipse.che.infrastructure.docker.client.json.network.Network;
import org.eclipse.che.infrastructure.docker.client.params.RemoveContainerParams;
import org.eclipse.che.workspace.infrastructure.docker.Labels;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test for {@link DockerAbandonedResourcesCleaner}
 *
 * @author Alexander Andrienko
 */
@Listeners(MockitoTestNGListener.class)
public class DockerAbandonedResourcesCleanerTest {

  private static final String machineName1 = "machine1";
  private static final String workspaceId1 = "workspaceid1";

  private static final String machineName2 = "machine2";
  private static final String workspaceId2 = "workspaceid2";

  private static final String containerName1 = "containerName1";
  private static final String containerId1 = "containerId1";

  private static final String containerName2 = "containerName2";
  private static final String containerId2 = "containerId2";

  private static final String containerName3 = "containerName3";
  private static final String containerId3 = "containerId3";

  private static final String EXITED_STATUS = "exited";
  private static final String RUNNING_STATUS = "Up 6 hour ago";

  private static final String abandonedNetworkId = "abandonedNetworkId";
  private static final String usedNetworkId = "usedNetworkId";

  private static final String abandonedNetworkName = "workspace1234567890abcdef_1234567890abcdef";
  private static final String usedNetworkName = "workspace0987654321zyxwvu_0987654321zyxwvu";

  @Mock private WorkspaceManager workspaceManager;
  @Mock private DockerConnector dockerConnector;
  @Mock private WorkspaceRuntimes workspaceRuntimes;

  @Mock private WorkspaceImpl workspaceImpl1;

  @Mock private WorkspaceImpl workspaceImpl2;

  @Mock private MachineImpl machineImpl;

  @Mock private RuntimeImpl runtimeImpl;
  @Mock private ContainerListEntry container1;
  @Mock private ContainerListEntry container2;
  @Mock private ContainerListEntry container3;

  @Mock private Network abandonedNetwork;
  @Mock private Network usedNetwork;
  @Mock private Network additionalNetwork;

  @Mock private ContainerInNetwork containerInNetwork1;

  private List<Network> networks;
  private Map<String, ContainerInNetwork> usedNetworkContainers;
  private Map<String, ContainerInNetwork> additionalNetworkContainers = new HashMap<>();

  private DockerAbandonedResourcesCleaner cleaner;

  @BeforeMethod
  public void setUp() throws Exception {
    networks = new ArrayList<>();
    usedNetworkContainers = new HashMap<>();

    cleaner =
        spy(
            new DockerAbandonedResourcesCleaner(
                workspaceManager, dockerConnector, workspaceRuntimes));

    doAnswer(
            invocation -> {
              String workspaceId = invocation.getArgument(0);
              switch (workspaceId) {
                case workspaceId1:
                  return workspaceImpl1;
                case workspaceId2:
                  return workspaceImpl2;
                default:
                  throw new NotFoundException("Workspace not found");
              }
            })
        .when(workspaceManager)
        .getWorkspace(anyString());

    when(workspaceImpl1.getRuntime()).thenReturn(runtimeImpl);
    doReturn(ImmutableMap.of(machineName1, machineImpl)).when(runtimeImpl).getMachines();

    when(workspaceManager.getWorkspace(workspaceId2)).thenReturn(workspaceImpl2);

    when(dockerConnector.listContainers()).thenReturn(asList(container1, container2, container3));

    when(container1.getNames()).thenReturn(new String[] {containerName1});
    when(container1.getLabels())
        .thenReturn(
            ImmutableMap.of(
                Labels.LABEL_MACHINE_NAME, machineName1, Labels.LABEL_WORKSPACE_ID, workspaceId1));
    when(container1.getStatus()).thenReturn(RUNNING_STATUS);
    when(container1.getId()).thenReturn(containerId1);

    when(container2.getNames()).thenReturn(new String[] {containerName2});
    when(container2.getLabels())
        .thenReturn(
            ImmutableMap.of(
                Labels.LABEL_MACHINE_NAME, machineName2, Labels.LABEL_WORKSPACE_ID, workspaceId2));
    when(container2.getStatus()).thenReturn(RUNNING_STATUS);
    when(container2.getId()).thenReturn(containerId2);

    when(container3.getNames()).thenReturn(new String[] {containerName3});
    when(container3.getLabels())
        .thenReturn(
            ImmutableMap.of(
                Labels.LABEL_MACHINE_NAME, machineName2, Labels.LABEL_WORKSPACE_ID, workspaceId2));
    when(container3.getStatus()).thenReturn(RUNNING_STATUS);
    when(container3.getId()).thenReturn(containerId3);

    when(dockerConnector.getNetworks(any())).thenReturn(networks);

    when(abandonedNetwork.getId()).thenReturn(abandonedNetworkId);
    when(usedNetwork.getId()).thenReturn(usedNetworkId);
    when(additionalNetwork.getId()).thenReturn(abandonedNetworkId);

    when(abandonedNetwork.getName()).thenReturn(abandonedNetworkName);
    when(usedNetwork.getName()).thenReturn(usedNetworkName);
    when(additionalNetwork.getName()).thenReturn(abandonedNetworkName);

    //          when(abandonedNetwork.getContainers()).thenReturn(abandonedNetworkContainers);
    when(usedNetwork.getContainers()).thenReturn(usedNetworkContainers);
    when(additionalNetwork.getContainers()).thenReturn(additionalNetworkContainers);
  }

  @Test
  public void cleanerShouldRunCleanOfContainerAndThenCleanOfNetworks() {
    // when
    cleaner.run();

    // then
    verify(cleaner).cleanContainers();
    verify(cleaner).cleanNetworks();
  }

  @Test
  public void cleanerShouldRunCleanNetworksEvenIfCleanOfContainersFailed() throws IOException {
    // given
    when(dockerConnector.listContainers())
        .thenThrow(new IOException("Error while fetching docker containers list"));

    // when
    cleaner.run();

    // then
    verify(cleaner).cleanNetworks();
  }

  @Test
  public void
      cleanerShouldKillAndRemoveContainerIfThisContainerIsRunningAndDoesntBelongToRunningWorkspace()
          throws Exception {
    cleaner.cleanContainers();

    verify(dockerConnector).listContainers();

    verify(workspaceManager, times(3)).getWorkspace(anyString());

    verify(dockerConnector, times(2)).killContainer(anyString());
    verify(dockerConnector, times(2)).removeContainer(any());

    verify(dockerConnector, never()).killContainer(containerId1);
    verify(dockerConnector, never())
        .removeContainer(
            RemoveContainerParams.create(containerId1).withForce(true).withRemoveVolumes(true));
  }

  @Test
  public void cleanerShouldRemoveButShouldNotKillContainerWithStatusNotRunning() throws Exception {
    when(container2.getStatus()).thenReturn(EXITED_STATUS);
    cleaner.cleanContainers();

    verify(dockerConnector, never()).killContainer(containerId2);
    verify(dockerConnector)
        .removeContainer(
            RemoveContainerParams.create(containerId2).withForce(true).withRemoveVolumes(true));
  }

  @Test
  public void shouldRemoveAbandonedNetwork() throws IOException {
    // given
    networks.add(abandonedNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector).removeNetwork(eq(abandonedNetworkId));
  }

  @Test
  public void shouldNotRemoveNetworkIfItNameNotMatchCheNetworkPattern() throws IOException {
    // given
    when(abandonedNetwork.getName()).thenReturn("UserNetwork");
    networks.add(abandonedNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector, never()).removeNetwork(eq(abandonedNetworkId));
  }

  @Test
  public void shouldNotRemoveNetworkWhenItContainsContainer() throws IOException {
    // given
    usedNetworkContainers.put(containerId1, containerInNetwork1);

    networks.add(usedNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector, never()).removeNetwork(eq(usedNetworkId));
  }

  @Test
  public void shouldNotRemoveNetworkWhichIsInWorkspaceRuntime() throws IOException {
    // given
    final String usedNetworkWorkspace = usedNetworkName.substring(0, 25);
    when(workspaceRuntimes.hasRuntime(usedNetworkWorkspace)).thenReturn(true);

    networks.add(usedNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector, never()).removeNetwork(eq(usedNetworkId));
  }

  @Test
  public void shouldRemoveOnlyAbandonedNetworks() throws IOException {
    // given
    usedNetworkContainers.put(containerId1, containerInNetwork1);

    networks.add(abandonedNetwork);
    networks.add(usedNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector).removeNetwork(abandonedNetworkId);
    verify(dockerConnector, never()).removeNetwork(usedNetworkId);
  }

  @Test
  public void shouldRemoveAbandonedNetworkEvenIfRemovingOfPreviousOneFailed() throws IOException {
    // given
    doThrow(new IOException("Failed to remove docker network"))
        .when(dockerConnector)
        .removeNetwork(usedNetworkId);

    networks.add(abandonedNetwork);
    networks.add(usedNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector).removeNetwork(abandonedNetworkId);
    verify(dockerConnector).removeNetwork(usedNetworkId);
  }

  @Test
  public void shouldBeAbleToRemoveSeveralAbandonedNetworks() throws IOException {
    // given
    final Network abandonedNetwork2 = mock(Network.class);
    final String abandonedNetwork2Id = "network2";
    when(abandonedNetwork2.getId()).thenReturn(abandonedNetwork2Id);
    when(abandonedNetwork2.getName()).thenReturn("workspace0w5kg95j93kd9a1l_cjmd8rbnf9j9dnso");
    when(abandonedNetwork2.getContainers()).thenReturn(new HashMap<>());

    final Network userNetwork = mock(Network.class);
    final String userNetworkId = "network4";
    when(userNetwork.getId()).thenReturn(userNetworkId);
    when(userNetwork.getName()).thenReturn("userNetwork");
    when(userNetwork.getContainers()).thenReturn(new HashMap<>());

    usedNetworkContainers.put(containerId1, containerInNetwork1);

    networks.add(usedNetwork);
    networks.add(abandonedNetwork);
    networks.add(abandonedNetwork2);
    networks.add(userNetwork);

    // when
    cleaner.cleanNetworks();

    // then
    verify(dockerConnector, never()).removeNetwork(usedNetworkId);
    verify(dockerConnector, never()).removeNetwork(userNetworkId);
    verify(dockerConnector).removeNetwork(abandonedNetworkId);
    verify(dockerConnector).removeNetwork(abandonedNetworkId);
  }
}
