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

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toMap;
import static org.eclipse.che.api.core.model.workspace.config.ServerConfig.INTERNAL_SERVER_ATTRIBUTE;
import static org.eclipse.che.commons.lang.NameGenerator.generate;
import static org.eclipse.che.workspace.infrastructure.openshift.Constants.CHE_ORIGINAL_NAME_LABEL;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.openshift.api.model.Route;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.che.api.core.model.workspace.config.ServerConfig;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;
import org.eclipse.che.workspace.infrastructure.openshift.provision.UniqueNamesProvisioner;

/**
 * Helps to modify {@link OpenShiftEnvironment} to make servers that are configured by {@link
 * ServerConfig} publicly or workspace-wide accessible.
 *
 * <p>To make server accessible it is needed to make sure that container port is declared, create
 * {@link Service}. To make it also publicly accessible it is needed to create corresponding {@link
 * Route} for exposing this port.
 *
 * <p>Created services and routes will have serialized servers which are exposed by the
 * corresponding object and machine name to which these servers belongs to.
 *
 * <p>Container, service and route are linked in the following way:
 *
 * <pre>
 * Pod
 * metadata:
 *   labels:
 *     type: web-app
 * spec:
 *   containers:
 *   ...
 *   - ports:
 *     - containerPort: 8080
 *       name: web-app
 *       protocol: TCP
 *   ...
 * </pre>
 *
 * Then services expose containers ports in the following way:
 *
 * <pre>
 * Service
 * metadata:
 *   name: service123
 * spec:
 *   selector:                        ---->> Pod.metadata.labels
 *     type: web-app
 *   ports:
 *     - name: web-app
 *       port: 8080
 *       targetPort: [8080|web-app]   ---->> Pod.spec.ports[0].[containerPort|name]
 *       protocol: TCP                ---->> Pod.spec.ports[0].protocol
 * </pre>
 *
 * Then corresponding route expose one of the service's port:
 *
 * <pre>
 * Route
 * ...
 * spec:
 *   to:
 *     name: dev-machine              ---->> Service.metadata.name
 *     targetPort: [8080|web-app]     ---->> Service.spec.ports[0].[port|name]
 * </pre>
 *
 * <p>For accessing publicly accessible server user will use route host. For accessing
 * workspace-wide accessible server user will use service name. Information about servers that are
 * exposed by route and/or service are stored in annotations of a route or service.
 *
 * @author Sergii Leshchenko
 * @author Alexander Garagatyi
 * @see Annotations
 */
public class ServerExposer {

  public static final int SERVER_UNIQUE_PART_SIZE = 8;
  public static final String SERVER_PREFIX = "server";

  private final String machineName;
  private final Container container;
  private final Pod pod;
  private final OpenShiftEnvironment openShiftEnvironment;

  public ServerExposer(
      String machineName, Pod pod, Container container, OpenShiftEnvironment openShiftEnvironment) {
    this.machineName = machineName;
    this.pod = pod;
    this.container = container;
    this.openShiftEnvironment = openShiftEnvironment;
  }

  /**
   * Exposes specified servers.
   *
   * <p>Note that created OpenShift objects will select the corresponding pods by {@link
   * Constants#CHE_ORIGINAL_NAME_LABEL} label. That should be added by {@link
   * UniqueNamesProvisioner}.
   *
   * @param servers servers to expose
   * @see UniqueNamesProvisioner#provision(OpenShiftEnvironment, RuntimeIdentity)
   */
  public void expose(Map<String, ? extends ServerConfig> servers) {
    Map<String, ServicePort> portToServicePort = exposePort(servers.values());
    Map<String, ServerConfig> internalServers = new HashMap<>();
    Map<String, ServerConfig> externalServers = new HashMap<>();

    servers.forEach(
        (key, value) -> {
          if ("true".equals(value.getAttributes().get(INTERNAL_SERVER_ATTRIBUTE))) {
            internalServers.put(key, value);
          } else {
            externalServers.put(key, value);
          }
        });

    Service service =
        new ServiceBuilder()
            .withName(generate(SERVER_PREFIX, SERVER_UNIQUE_PART_SIZE) + '-' + machineName)
            .withMachineName(machineName)
            .withSelectorEntry(CHE_ORIGINAL_NAME_LABEL, pod.getMetadata().getName())
            .withPorts(new ArrayList<>(portToServicePort.values()))
            .withServers(internalServers)
            .build();

    String serviceName = service.getMetadata().getName();
    openShiftEnvironment.getServices().put(serviceName, service);

    for (ServicePort servicePort : portToServicePort.values()) {
      int port = servicePort.getTargetPort().getIntVal();
      Map<String, ServerConfig> routesServers =
          externalServers
              .entrySet()
              .stream()
              .filter(e -> parseInt(e.getValue().getPort().split("/")[0]) == port)
              .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

      Route route =
          new RouteBuilder()
              .withName(serviceName + '-' + servicePort.getName())
              .withMachineName(machineName)
              .withTargetPort(servicePort.getName())
              .withServers(routesServers)
              .withTo(serviceName)
              .build();
      openShiftEnvironment.getRoutes().put(route.getMetadata().getName(), route);
    }
  }

  private Map<String, ServicePort> exposePort(Collection<? extends ServerConfig> serverConfig) {
    Map<String, ServicePort> exposedPorts = new HashMap<>();
    Set<String> portsToExpose =
        serverConfig.stream().map(ServerConfig::getPort).collect(Collectors.toSet());

    for (String portToExpose : portsToExpose) {
      String[] portProtocol = portToExpose.split("/");
      int port = parseInt(portProtocol[0]);
      String protocol = portProtocol.length > 1 ? portProtocol[1].toUpperCase() : "TCP";
      Optional<ContainerPort> exposedOpt =
          container
              .getPorts()
              .stream()
              .filter(p -> p.getContainerPort().equals(port) && protocol.equals(p.getProtocol()))
              .findAny();
      ContainerPort containerPort;

      if (exposedOpt.isPresent()) {
        containerPort = exposedOpt.get();
      } else {
        containerPort =
            new ContainerPortBuilder().withContainerPort(port).withProtocol(protocol).build();
        container.getPorts().add(containerPort);
      }

      exposedPorts.put(
          portToExpose,
          new ServicePortBuilder()
              .withName("server-" + containerPort.getContainerPort())
              .withPort(containerPort.getContainerPort())
              .withProtocol(protocol)
              .withNewTargetPort(containerPort.getContainerPort())
              .build());
    }
    return exposedPorts;
  }

  private static class ServiceBuilder {
    private String name;
    private String machineName;
    private Map<String, String> selector = new HashMap<>();
    private List<ServicePort> ports = Collections.emptyList();
    private Map<String, ? extends ServerConfig> serversConfigs = Collections.emptyMap();

    private ServiceBuilder withName(String name) {
      this.name = name;
      return this;
    }

    private ServiceBuilder withSelectorEntry(String key, String value) {
      selector.put(key, value);
      return this;
    }

    private ServiceBuilder withPorts(List<ServicePort> ports) {
      this.ports = ports;
      return this;
    }

    private ServiceBuilder withServers(Map<String, ? extends ServerConfig> serversConfigs) {
      this.serversConfigs = serversConfigs;
      return this;
    }

    private Service build() {
      io.fabric8.kubernetes.api.model.ServiceBuilder builder =
          new io.fabric8.kubernetes.api.model.ServiceBuilder();
      return builder
          .withNewMetadata()
          .withName(name.replace("/", "-"))
          .withAnnotations(
              Annotations.newSerializer()
                  .servers(serversConfigs)
                  .machineName(machineName)
                  .annotations())
          .endMetadata()
          .withNewSpec()
          .withSelector(selector)
          .withPorts(ports)
          .endSpec()
          .build();
    }

    public ServiceBuilder withMachineName(String machineName) {
      this.machineName = machineName;
      return this;
    }
  }

  private static class RouteBuilder {
    private String name;
    private String serviceName;
    private IntOrString targetPort;
    private Map<String, ? extends ServerConfig> serversConfigs;
    private String machineName;

    private RouteBuilder withName(String name) {
      this.name = name;
      return this;
    }

    private RouteBuilder withTo(String serviceName) {
      this.serviceName = serviceName;
      return this;
    }

    private RouteBuilder withTargetPort(String targetPortName) {
      this.targetPort = new IntOrString(targetPortName);
      return this;
    }

    private RouteBuilder withServers(Map<String, ? extends ServerConfig> serversConfigs) {
      this.serversConfigs = serversConfigs;
      return this;
    }

    public RouteBuilder withMachineName(String machineName) {
      this.machineName = machineName;
      return this;
    }

    private Route build() {
      io.fabric8.openshift.api.model.RouteBuilder builder =
          new io.fabric8.openshift.api.model.RouteBuilder();

      return builder
          .withNewMetadata()
          .withName(name.replace("/", "-"))
          .withAnnotations(
              Annotations.newSerializer()
                  .servers(serversConfigs)
                  .machineName(machineName)
                  .annotations())
          .endMetadata()
          .withNewSpec()
          .withNewTo()
          .withName(serviceName)
          .endTo()
          .withNewPort()
          .withTargetPort(targetPort)
          .endPort()
          .endSpec()
          .build();
    }
  }
}
