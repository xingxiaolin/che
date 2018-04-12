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

import static java.util.Collections.emptyList;
import static org.eclipse.che.api.core.model.workspace.runtime.MachineStatus.STARTING;
import static org.eclipse.che.dto.server.DtoFactory.newDto;
import static org.eclipse.che.workspace.infrastructure.kubernetes.Constants.CHE_ORIGINAL_NAME_LABEL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.IntOrStringBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteSpec;
import io.fabric8.openshift.api.model.RouteTargetReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.che.api.core.model.workspace.runtime.MachineStatus;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.installer.server.model.impl.InstallerImpl;
import org.eclipse.che.api.workspace.server.DtoConverter;
import org.eclipse.che.api.workspace.server.URLRewriter;
import org.eclipse.che.api.workspace.server.hc.ServersChecker;
import org.eclipse.che.api.workspace.server.hc.ServersCheckerFactory;
import org.eclipse.che.api.workspace.server.hc.probe.ProbeScheduler;
import org.eclipse.che.api.workspace.server.hc.probe.WorkspaceProbesFactory;
import org.eclipse.che.api.workspace.server.model.impl.RuntimeIdentityImpl;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.api.workspace.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.workspace.infrastructure.kubernetes.bootstrapper.KubernetesBootstrapper;
import org.eclipse.che.workspace.infrastructure.kubernetes.bootstrapper.KubernetesBootstrapperFactory;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesPods;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesServices;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.pvc.WorkspaceVolumesStrategy;
import org.eclipse.che.workspace.infrastructure.kubernetes.util.KubernetesSharedPool;
import org.eclipse.che.workspace.infrastructure.kubernetes.util.RuntimeEventsPublisher;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;
import org.eclipse.che.workspace.infrastructure.openshift.project.OpenShiftProject;
import org.eclipse.che.workspace.infrastructure.openshift.project.OpenShiftRoutes;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests {@link OpenShiftInternalRuntime}.
 *
 * @author Anton Korneta
 */
public class OpenShiftInternalRuntimeTest {
  private static final int EXPOSED_PORT_1 = 4401;
  private static final int EXPOSED_PORT_2 = 8081;
  private static final int INTERNAL_PORT = 4411;

  private static final String WORKSPACE_ID = "workspace123";
  private static final String POD_NAME = "app";
  private static final String ROUTE_NAME = "test-route";
  private static final String SERVICE_NAME = "test-service";
  private static final String POD_SELECTOR = "che.pod.name";
  private static final String CONTAINER_NAME_1 = "test1";
  private static final String CONTAINER_NAME_2 = "test2";
  private static final String ROUTE_HOST = "localhost";
  private static final String M1_NAME = POD_NAME + '/' + CONTAINER_NAME_1;
  private static final String M2_NAME = POD_NAME + '/' + CONTAINER_NAME_2;

  private static final RuntimeIdentity IDENTITY =
      new RuntimeIdentityImpl(WORKSPACE_ID, "env1", "id1");

  @Mock private OpenShiftRuntimeContext context;
  @Mock private EventService eventService;
  @Mock private ServersCheckerFactory serverCheckerFactory;
  @Mock private ServersChecker serversChecker;
  @Mock private KubernetesBootstrapperFactory bootstrapperFactory;
  @Mock private OpenShiftEnvironment osEnv;
  @Mock private OpenShiftProject project;
  @Mock private KubernetesServices services;
  @Mock private OpenShiftRoutes routes;
  @Mock private KubernetesPods pods;
  @Mock private KubernetesBootstrapper bootstrapper;
  @Mock private WorkspaceVolumesStrategy volumesStrategy;
  @Mock private WorkspaceProbesFactory workspaceProbesFactory;
  @Mock private ProbeScheduler probesScheduler;

  @Captor private ArgumentCaptor<MachineStatusEvent> machineStatusEventCaptor;

  private OpenShiftInternalRuntime internalRuntime;

  private Map<String, Service> allServices;
  private Map<String, Route> allRoutes;

  @BeforeMethod
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    internalRuntime =
        new OpenShiftInternalRuntime(
            13,
            5,
            new URLRewriter.NoOpURLRewriter(),
            bootstrapperFactory,
            serverCheckerFactory,
            volumesStrategy,
            probesScheduler,
            workspaceProbesFactory,
            new RuntimeEventsPublisher(eventService),
            mock(KubernetesSharedPool.class),
            context,
            project,
            emptyList());
    when(context.getEnvironment()).thenReturn(osEnv);
    when(serverCheckerFactory.create(any(), anyString(), any())).thenReturn(serversChecker);
    when(context.getIdentity()).thenReturn(IDENTITY);
    doNothing().when(project).cleanUp();
    when(project.services()).thenReturn(services);
    when(project.routes()).thenReturn(routes);
    when(project.pods()).thenReturn(pods);
    when(bootstrapperFactory.create(any(), anyList(), any())).thenReturn(bootstrapper);
    doReturn(
            ImmutableMap.of(
                M1_NAME,
                mockMachine(mockInstaller("ws-agent")),
                M2_NAME,
                mockMachine(mockInstaller("terminal"))))
        .when(osEnv)
        .getMachines();
    allServices = ImmutableMap.of(SERVICE_NAME, mockService());
    allRoutes = ImmutableMap.of(SERVICE_NAME, mockRoute());
    final Container container = mockContainer(CONTAINER_NAME_1, EXPOSED_PORT_1, INTERNAL_PORT);
    final ImmutableMap<String, Pod> allPods =
        ImmutableMap.of(POD_NAME, mockPod(ImmutableList.of(container)));
    when(services.create(any())).thenAnswer(a -> a.getArguments()[0]);
    when(routes.create(any())).thenAnswer(a -> a.getArguments()[0]);
    when(pods.create(any())).thenAnswer(a -> a.getArguments()[0]);
    when(osEnv.getServices()).thenReturn(allServices);
    when(osEnv.getRoutes()).thenReturn(allRoutes);
    when(osEnv.getPods()).thenReturn(allPods);
  }

  @Test
  public void shouldStartMachines() throws Exception {
    final Container container1 = mockContainer(CONTAINER_NAME_1, EXPOSED_PORT_1);
    final Container container2 = mockContainer(CONTAINER_NAME_2, EXPOSED_PORT_2, INTERNAL_PORT);
    final ImmutableMap<String, Pod> allPods =
        ImmutableMap.of(POD_NAME, mockPod(ImmutableList.of(container1, container2)));
    when(osEnv.getPods()).thenReturn(allPods);

    internalRuntime.startMachines();

    verify(pods).create(any());
    verify(routes).create(any());
    verify(services).create(any());

    verify(eventService, times(2)).publish(any());
    verifyEventsOrder(newEvent(M1_NAME, STARTING), newEvent(M2_NAME, STARTING));
  }

  private static MachineStatusEvent newEvent(String machineName, MachineStatus status) {
    return newDto(MachineStatusEvent.class)
        .withIdentity(DtoConverter.asDto(IDENTITY))
        .withMachineName(machineName)
        .withEventType(status);
  }

  private void verifyEventsOrder(MachineStatusEvent... expectedEvents) {
    final Iterator<MachineStatusEvent> actualEvents = captureEvents().iterator();
    for (MachineStatusEvent expected : expectedEvents) {
      if (!actualEvents.hasNext()) {
        fail("It is expected to receive machine status events");
      }
      final MachineStatusEvent actual = actualEvents.next();
      assertEquals(actual, expected);
    }
    if (actualEvents.hasNext()) {
      fail("No more events expected");
    }
  }

  private List<MachineStatusEvent> captureEvents() {
    verify(eventService, atLeastOnce()).publish(machineStatusEventCaptor.capture());
    return machineStatusEventCaptor.getAllValues();
  }

  private static Container mockContainer(String name, int... ports) {
    final Container container = mock(Container.class);
    when(container.getName()).thenReturn(name);
    final List<ContainerPort> containerPorts = new ArrayList<>(ports.length);
    for (int port : ports) {
      containerPorts.add(new ContainerPortBuilder().withContainerPort(port).build());
    }
    when(container.getPorts()).thenReturn(containerPorts);
    return container;
  }

  private static Pod mockPod(List<Container> containers) {
    final Pod pod = mock(Pod.class);
    final PodSpec spec = mock(PodSpec.class);
    mockName(POD_NAME, pod);
    when(spec.getContainers()).thenReturn(containers);
    when(pod.getSpec()).thenReturn(spec);
    when(pod.getMetadata().getLabels())
        .thenReturn(ImmutableMap.of(POD_SELECTOR, POD_NAME, CHE_ORIGINAL_NAME_LABEL, POD_NAME));
    return pod;
  }

  private static Service mockService() {
    final Service service = mock(Service.class);
    final ServiceSpec spec = mock(ServiceSpec.class);
    mockName(SERVICE_NAME, service);
    when(service.getSpec()).thenReturn(spec);
    when(spec.getSelector()).thenReturn(ImmutableMap.of(POD_SELECTOR, POD_NAME));
    final ServicePort sp1 =
        new ServicePortBuilder().withTargetPort(intOrString(EXPOSED_PORT_1)).build();
    final ServicePort sp2 =
        new ServicePortBuilder().withTargetPort(intOrString(EXPOSED_PORT_2)).build();
    when(spec.getPorts()).thenReturn(ImmutableList.of(sp1, sp2));
    return service;
  }

  private static Route mockRoute() {
    final Route route = mock(Route.class);
    mockName(ROUTE_NAME, route);
    final RouteSpec spec = mock(RouteSpec.class);
    final RouteTargetReference target = mock(RouteTargetReference.class);
    when(target.getName()).thenReturn(SERVICE_NAME);
    when(spec.getTo()).thenReturn(target);
    when(spec.getHost()).thenReturn(ROUTE_HOST);
    when(route.getSpec()).thenReturn(spec);
    when(route.getMetadata().getLabels())
        .thenReturn(ImmutableMap.of(CHE_ORIGINAL_NAME_LABEL, ROUTE_NAME));
    return route;
  }

  private static InstallerImpl mockInstaller(String name) {
    InstallerImpl installer = mock(InstallerImpl.class);
    when(installer.getName()).thenReturn(name);
    return installer;
  }

  private static InternalMachineConfig mockMachine(InstallerImpl... installers) {
    final InternalMachineConfig machine1 = mock(InternalMachineConfig.class);
    when(machine1.getInstallers()).thenReturn(Arrays.asList(installers));
    return machine1;
  }

  private static ObjectMeta mockName(String name, HasMetadata mock) {
    final ObjectMeta metadata = mock(ObjectMeta.class);
    when(mock.getMetadata()).thenReturn(metadata);
    when(metadata.getName()).thenReturn(name);
    return metadata;
  }

  private static IntOrString intOrString(int port) {
    return new IntOrStringBuilder().withIntVal(port).withStrVal(String.valueOf(port)).build();
  }
}
