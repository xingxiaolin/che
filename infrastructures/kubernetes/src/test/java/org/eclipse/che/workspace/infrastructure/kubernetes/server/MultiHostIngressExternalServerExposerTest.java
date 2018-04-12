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
package org.eclipse.che.workspace.infrastructure.kubernetes.server;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.eclipse.che.workspace.infrastructure.kubernetes.server.KubernetesServerExposer.SERVER_PREFIX;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressBackend;
import io.fabric8.kubernetes.api.model.extensions.IngressRule;
import java.util.Map;
import org.eclipse.che.api.core.model.workspace.config.ServerConfig;
import org.eclipse.che.api.workspace.server.model.impl.ServerConfigImpl;
import org.eclipse.che.workspace.infrastructure.kubernetes.Annotations;
import org.eclipse.che.workspace.infrastructure.kubernetes.environment.KubernetesEnvironment;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** @author Guy Daich */
public class MultiHostIngressExternalServerExposerTest {

  private static final Map<String, String> ATTRIBUTES_MAP = singletonMap("key", "value");
  private static final String MACHINE_NAME = "pod/main";
  private static final String SERVICE_NAME = SERVER_PREFIX + "12345678" + "-" + MACHINE_NAME;
  private static final String DOMAIN = "che.com";

  private MultiHostIngressExternalServerExposer externalServerExposer;
  private KubernetesEnvironment kubernetesEnvironment;

  @BeforeMethod
  public void setUp() throws Exception {
    Container container = new ContainerBuilder().withName("main").build();
    Pod pod =
        new PodBuilder()
            .withNewMetadata()
            .withName("pod")
            .endMetadata()
            .withNewSpec()
            .withContainers(container)
            .endSpec()
            .build();

    kubernetesEnvironment =
        KubernetesEnvironment.builder().setPods(ImmutableMap.of("pod", pod)).build();
    externalServerExposer = new MultiHostIngressExternalServerExposer(emptyMap(), DOMAIN);
  }

  @Test
  public void shouldCreateIngressForServer() {
    // given
    ServerConfigImpl httpServerConfig =
        new ServerConfigImpl("8080/tcp", "http", "/api", ATTRIBUTES_MAP);
    IntOrString targetPort = new IntOrString(8080);
    ServicePort servicePort =
        new ServicePortBuilder()
            .withName("server-8080")
            .withPort(8080)
            .withProtocol("TCP")
            .withTargetPort(targetPort)
            .build();
    Map<String, ServicePort> portToServicePort = ImmutableMap.of("8080/tcp", servicePort);
    Map<String, ServerConfig> serversToExpose = ImmutableMap.of("http-server", httpServerConfig);

    // when
    externalServerExposer.exposeExternalServers(
        kubernetesEnvironment, MACHINE_NAME, SERVICE_NAME, portToServicePort, serversToExpose);

    // then
    assertThatExternalServerIsExposed(
        MACHINE_NAME,
        SERVICE_NAME,
        "http-server",
        "tcp",
        8080,
        servicePort,
        new ServerConfigImpl(httpServerConfig).withAttributes(ATTRIBUTES_MAP));
  }

  @Test
  public void shouldCreateIngressForServerWhenTwoServersHasTheSamePort() {
    // given
    ServerConfigImpl httpServerConfig =
        new ServerConfigImpl("8080/tcp", "http", "/api", ATTRIBUTES_MAP);
    ServerConfigImpl wsServerConfig =
        new ServerConfigImpl("8080/tcp", "ws", "/connect", ATTRIBUTES_MAP);
    IntOrString targetPort = new IntOrString(8080);

    ServicePort servicePort =
        new ServicePortBuilder()
            .withName("server-8080")
            .withPort(8080)
            .withProtocol("TCP")
            .withTargetPort(targetPort)
            .build();
    Map<String, ServicePort> portToServicePort = ImmutableMap.of("8080/tcp", servicePort);

    Map<String, ServerConfig> serversToExpose =
        ImmutableMap.of(
            "http-server", httpServerConfig,
            "ws-server", wsServerConfig);

    // when
    externalServerExposer.exposeExternalServers(
        kubernetesEnvironment, MACHINE_NAME, SERVICE_NAME, portToServicePort, serversToExpose);

    // then
    assertEquals(kubernetesEnvironment.getIngresses().size(), 1);
    assertThatExternalServerIsExposed(
        MACHINE_NAME,
        SERVICE_NAME,
        "http-server",
        "tcp",
        8080,
        servicePort,
        new ServerConfigImpl(httpServerConfig).withAttributes(ATTRIBUTES_MAP));
    assertThatExternalServerIsExposed(
        MACHINE_NAME,
        SERVICE_NAME,
        "ws-server",
        "tcp",
        8080,
        servicePort,
        new ServerConfigImpl(wsServerConfig).withAttributes(ATTRIBUTES_MAP));
  }

  @Test
  public void shouldCreateIngressesForServerWhenTwoServersHasDifferentPorts() {
    // given
    ServerConfigImpl httpServerConfig =
        new ServerConfigImpl("8080/tcp", "http", "/api", ATTRIBUTES_MAP);
    ServerConfigImpl wsServerConfig =
        new ServerConfigImpl("8081/tcp", "ws", "/connect", ATTRIBUTES_MAP);
    IntOrString httpTargetPort = new IntOrString(8080);
    IntOrString wsTargetPort = new IntOrString(8081);
    ServicePort httpServicePort =
        new ServicePortBuilder()
            .withName("server-8080")
            .withPort(8080)
            .withProtocol("TCP")
            .withTargetPort(httpTargetPort)
            .build();
    ServicePort wsServicePort =
        new ServicePortBuilder()
            .withName("server-8081")
            .withPort(8081)
            .withProtocol("TCP")
            .withTargetPort(wsTargetPort)
            .build();
    Map<String, ServicePort> portToServicePort =
        ImmutableMap.of("8080/tcp", httpServicePort, "8081/tcp", wsServicePort);

    Map<String, ServerConfig> serversToExpose =
        ImmutableMap.of(
            "http-server", httpServerConfig,
            "ws-server", wsServerConfig);

    // when
    externalServerExposer.exposeExternalServers(
        kubernetesEnvironment, MACHINE_NAME, SERVICE_NAME, portToServicePort, serversToExpose);

    // then
    assertEquals(kubernetesEnvironment.getIngresses().size(), 2);
    assertThatExternalServerIsExposed(
        MACHINE_NAME,
        SERVICE_NAME,
        "http-server",
        "tcp",
        8080,
        httpServicePort,
        new ServerConfigImpl(httpServerConfig).withAttributes(ATTRIBUTES_MAP));
    assertThatExternalServerIsExposed(
        MACHINE_NAME,
        SERVICE_NAME,
        "ws-server",
        "tcp",
        8081,
        wsServicePort,
        new ServerConfigImpl(wsServerConfig).withAttributes(ATTRIBUTES_MAP));
  }

  private void assertThatExternalServerIsExposed(
      String machineName,
      String serviceName,
      String serverNameRegex,
      String portProtocol,
      Integer port,
      ServicePort servicePort,
      ServerConfigImpl expected) {

    // ensure that required ingress is created
    Ingress ingress = kubernetesEnvironment.getIngresses().get(serviceName + "-server-" + port);
    IngressRule ingressRule = ingress.getSpec().getRules().get(0);
    assertEquals(ingressRule.getHost(), serviceName + "-" + servicePort.getName() + "." + DOMAIN);
    assertEquals(ingressRule.getHttp().getPaths().get(0).getPath(), "/");
    IngressBackend backend = ingressRule.getHttp().getPaths().get(0).getBackend();
    assertEquals(backend.getServiceName(), serviceName);
    assertEquals(backend.getServicePort().getStrVal(), servicePort.getName());

    Annotations.Deserializer ingressAnnotations =
        Annotations.newDeserializer(ingress.getMetadata().getAnnotations());
    Map<String, ServerConfigImpl> servers = ingressAnnotations.servers();
    ServerConfig serverConfig = servers.get(serverNameRegex);
    assertEquals(serverConfig, expected);

    assertEquals(ingressAnnotations.machineName(), machineName);
  }
}
