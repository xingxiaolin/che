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

import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.che.api.core.model.workspace.config.ServerConfig;

/**
 * Provides a path-based strategy for exposing service ports outside the cluster using Ingress
 * Ingresses will be created with a common host name for all workspaces.
 *
 * <p>This strategy uses different Ingress path entries <br>
 * Each external server is exposed with a unique path prefix.
 *
 * <p>This strategy imposes limitation on user-developed applications. <br>
 *
 * <pre>
 *   Path-Based Ingress exposing service's port:
 * Ingress
 * ...
 * spec:
 *   rules:
 *     - host: CHE_HOST
 *       http:
 *         paths:
 *           - path: service123/webapp        ---->> Service.metadata.name + / + Service.spec.ports[0].name
 *             backend:
 *               serviceName: service123      ---->> Service.metadata.name
 *               servicePort: [8080|web-app]  ---->> Service.spec.ports[0].[port|name]
 * </pre>
 *
 * @author Sergii Leshchenko
 * @author Guy Daich
 */
public class SingleHostIngressExternalServerExposer
    extends AbstractIngressExternalServerExposerStrategy {

  public static final String SINGLE_HOST_STRATEGY = "single-host";
  private final Map<String, String> ingressAnnotations;
  private final String cheHost;

  @Inject
  public SingleHostIngressExternalServerExposer(
      @Named("infra.kubernetes.ingress.annotations") Map<String, String> ingressAnnotations,
      @Named("che.host") String cheHost) {
    this.ingressAnnotations = ingressAnnotations;
    this.cheHost = cheHost;
  }

  @Override
  protected Ingress generateIngress(
      String machineName,
      String serviceName,
      ServicePort servicePort,
      Map<String, ServerConfig> ingressesServers) {
    return new ExternalServerIngressBuilder()
        .withHost(cheHost)
        .withPath(generateExternalServerIngressPath(serviceName, servicePort))
        .withName(generateExternalServerIngressName(serviceName, servicePort))
        .withMachineName(machineName)
        .withServiceName(serviceName)
        .withAnnotations(ingressAnnotations)
        .withServicePort(servicePort.getName())
        .withServers(ingressesServers)
        .build();
  }

  private String generateExternalServerIngressName(String serviceName, ServicePort servicePort) {
    return serviceName + '-' + servicePort.getName();
  }

  private String generateExternalServerIngressPath(String serviceName, ServicePort servicePort) {
    return "/" + serviceName + "/" + servicePort.getName();
  }
}
