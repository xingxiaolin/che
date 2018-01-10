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

import com.google.common.annotations.VisibleForTesting;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Pod;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import org.eclipse.che.api.core.model.workspace.config.ServerConfig;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.installer.server.model.impl.InstallerImpl;
import org.eclipse.che.api.installer.server.model.impl.InstallerServerConfigImpl;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.workspace.infrastructure.openshift.Names;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;

/**
 * @author Sergii Leshchenko
 */
public class InstallerPortProvisioner implements ConfigurationProvisioner {

  @Override
  public void provision(OpenShiftEnvironment osEnv, RuntimeIdentity identity)
      throws InfrastructureException {
    for (Pod pod : osEnv.getPods().values()) {
      List<Container> containers = pod.getSpec().getContainers();
      if (containers.size() < 2) {
        continue;
      }

      Map<String, InternalMachineConfig> machines = new HashMap<>();
      for (Container container : containers) {
        String machineName = Names.machineName(pod, container);
        InternalMachineConfig machineConfig = osEnv.getMachines().get(machineName);
        machines.put(machineName, machineConfig);
      }

      fixPortConflicts(machines);
    }
  }

  @VisibleForTesting
  void fixPortConflicts(Map<String, InternalMachineConfig> machines) {
    Set<String> occupiedPorts = new HashSet<>();

    for (Entry<String, InternalMachineConfig> machineConfigEntry : machines.entrySet()) {
      InternalMachineConfig machineConfig = machineConfigEntry.getValue();



      for (InstallerImpl installer : machineConfig.getInstallers()) {
        for (Entry<String, ? extends ServerConfig> serverEntry : installer.getServers()
            .entrySet()) {
          String serverName = serverEntry.getKey();
          ServerConfig serverConfig = serverEntry.getValue();

          String[] splittedPort = serverConfig.getPort().split("/");
          String portValue = splittedPort[0];
          String protocol = splittedPort[1];
          if (!occupiedPorts.add(portValue)) {
            InstallerServerConfigImpl newServerConfig = new InstallerServerConfigImpl(
                serverConfig);

            Random random = new Random();
            int newPort = generatePort(random); // 10_000 -> 20_000
            String newPortStr = Integer.toString(newPort);

            while (occupiedPorts.contains(newPortStr)) {
              newPort = generatePort(random); // 10_000 -> 20_000
              newPortStr = Integer.toString(newPort);
            }

            newServerConfig.setPort(newPortStr + "/" + protocol);

            occupiedPorts.add(newPortStr);

            installer.getServers().put(serverName, newServerConfig);
            machineConfig.getServers().put(serverName, newServerConfig);

            machineConfig.getEnv().put(getEnvName(serverName), newPortStr);
          }
        }
      }
    }
  }

  @VisibleForTesting
  String getEnvName(String serverName) {
    String serverNameEnv = serverName.replaceAll("[^\\d\\w]", "_");
    return "CHE_SERVER_" + serverNameEnv.toUpperCase() + "_PORT";
  }

  private int generatePort(Random random) {
    return random.nextInt(10_000) + 10_001;
  }
}
