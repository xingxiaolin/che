/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.workspace.infrastructure.openshift.provision;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.che.api.installer.server.model.impl.InstallerImpl;
import org.eclipse.che.api.workspace.server.model.impl.ServerConfigImpl;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test {@link InstallerPortProvisioner}.
 *
 * @author Sergii Leshchenko
 */
@Listeners(MockitoTestNGListener.class)
public class InstallerPortProvisionerTest {

  private final InstallerPortProvisioner portProvisioner = new InstallerPortProvisioner();

  @Test
  public void testEnvVarName() {
    String envName = portProvisioner.getEnvName("terminal/pty|ws-8080");

    assertEquals(envName, "CHE_SERVER_TERMINAL_PTY_WS_8080_PORT");
  }

  @Test
  public void testPortsConflictsResolving() throws Exception {
    Map<String, ServerConfigImpl> servers1 = new HashMap<>();
    servers1.put("server1", new ServerConfigImpl("8080/tcp", "http", "/api", emptyMap()));

    InstallerImpl installer1 =
        new InstallerImpl(
            "installer1", "name", "v1", "description", emptyList(), emptyMap(), "script", servers1);

    InternalMachineConfig machine1 =
        new InternalMachineConfig(
            singletonList(installer1), servers1, new HashMap<>(), emptyMap(), emptyMap());

    Map<String, ServerConfigImpl> servers2 = new HashMap<>();
    servers2.put("server2-http", new ServerConfigImpl("8080/tcp", "http", "/api", emptyMap()));
    servers2.put("server2-ws", new ServerConfigImpl("8080/tcp", "ws", "/api", emptyMap()));

    InstallerImpl installer2 =
        new InstallerImpl(
            "installer2", "name", "v1", "description", emptyList(), emptyMap(), "script", servers2);

    InternalMachineConfig machine2 =
        new InternalMachineConfig(
            singletonList(installer2), servers2, new HashMap<>(), emptyMap(), emptyMap());

    portProvisioner.fixPortConflicts(ImmutableMap.of("machine1", machine1, "machine2", machine2));

    assertTrue(machine1.getEnv().isEmpty());
    assertEquals(machine1.getServers().get("server1").getPort(), "8080/tcp");
    assertEquals(machine1.getInstallers().get(0).getServers().get("server1").getPort(), "8080/tcp");

    String newPortEnv = machine2.getEnv().get("CHE_SERVER_SERVER2_HTTP_PORT");
    assertTrue(Pattern.compile("\\d{5}").matcher(newPortEnv).matches());

    Pattern portPattern = Pattern.compile("\\d{5}/tcp");
    String newHttpServerPort = machine2.getServers().get("server2-http").getPort();
    String newWsServerPort = machine2.getServers().get("server2-ws").getPort();
    assertTrue(portPattern.matcher(newHttpServerPort).matches());
    assertTrue(portPattern.matcher(newWsServerPort).matches());
    assertEquals(newHttpServerPort, newWsServerPort);

    String newInstallerHttpServerPort =
        machine2.getInstallers().get(0).getServers().get("server2-http").getPort();
    assertTrue(portPattern.matcher(newInstallerHttpServerPort).matches());

    String newInstallerWsServerPort =
        machine2.getInstallers().get(0).getServers().get("server2-ws").getPort();
    assertTrue(portPattern.matcher(newInstallerWsServerPort).matches());

    assertEquals(newHttpServerPort, newInstallerHttpServerPort);
    assertEquals(newWsServerPort, newInstallerWsServerPort);
  }
}
