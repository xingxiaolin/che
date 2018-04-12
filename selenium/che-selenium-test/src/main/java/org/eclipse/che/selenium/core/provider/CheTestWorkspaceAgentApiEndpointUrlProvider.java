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
package org.eclipse.che.selenium.core.provider;

import static org.eclipse.che.api.workspace.server.WsAgentMachineFinderUtil.containsWsAgentServer;
import static org.eclipse.che.api.workspace.shared.Constants.SERVER_WS_AGENT_HTTP_REFERENCE;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.URL;
import java.util.Map;
import org.eclipse.che.api.core.model.workspace.runtime.Machine;
import org.eclipse.che.api.core.model.workspace.runtime.Server;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;

@Singleton
public class CheTestWorkspaceAgentApiEndpointUrlProvider
    implements TestWorkspaceAgentApiEndpointUrlProvider {

  @Inject private HttpJsonRequestFactory httpJsonRequestFactory;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;

  @Override
  public URL get(String workspaceId) throws Exception {
    workspaceServiceClient.ensureRunningStatus(workspaceServiceClient.getById(workspaceId));

    Map<String, ? extends Machine> machines =
        workspaceServiceClient.getById(workspaceId).getRuntime().getMachines();
    for (Machine machine : machines.values()) {
      if (containsWsAgentServer(machine)) {
        Server wsAgentServer = machine.getServers().get(SERVER_WS_AGENT_HTTP_REFERENCE);
        if (wsAgentServer != null) {
          return new URL(wsAgentServer.getUrl() + "/");
        } else {
          throw new RuntimeException("Workspace agent server is null");
        }
      }
    }
    throw new RuntimeException("Cannot find dev machine on workspace with id " + workspaceId);
  }
}
