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
package org.eclipse.che.workspace.infrastructure.kubernetes.namespace.pvc;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesObjectUtil.newPVC;
import static org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesObjectUtil.newVolume;
import static org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesObjectUtil.newVolumeMount;
import static org.eclipse.che.workspace.infrastructure.kubernetes.provision.LogsVolumeMachineProvisioner.LOGS_VOLUME_NAME;

import com.google.inject.Inject;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.inject.Named;
import org.eclipse.che.api.core.model.workspace.config.Volume;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.workspace.infrastructure.kubernetes.Names;
import org.eclipse.che.workspace.infrastructure.kubernetes.environment.KubernetesEnvironment;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesNamespace;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesNamespaceFactory;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.KubernetesPersistentVolumeClaims;

/**
 * Provides common PVC for each workspace in one Kubernetes namespace.
 *
 * <p>This strategy uses subpaths for resolving backed up data paths collisions. <br>
 * Subpaths evaluated as following: '{workspaceId}/{workspace data folder}'. Workspace data folder
 * it's a configured path where workspace projects, logs or any other data located, this path may
 * contain a machine name when conflicts can occur e.g. two identical agents inside different
 * machines produce the same log file.
 *
 * <p>This strategy indirectly affects the workspace limits. <br>
 * The number of workspaces that can simultaneously store backups in one PV is limited only by the
 * storage capacity. The number of workspaces that can be running simultaneously depends on access
 * mode configuration and Che configuration limits.
 *
 * @author Anton Korneta
 * @author Alexander Garagatyi
 */
public class CommonPVCStrategy implements WorkspaceVolumesStrategy {

  public static final String COMMON_STRATEGY = "common";

  /**
   * The additional property name with the wildcard reserved for workspace id. Formatted property
   * with the real workspace id is used to get workspace subpaths directories. The value of this
   * property represents the String array of subpaths that are used to create folders in PV with
   * user rights. Note that the value would not be stored and it is removed before PVC creation.
   */
  static final String SUBPATHS_PROPERTY_FMT = "che.workspace.%s.subpaths";

  private final boolean preCreateDirs;
  private final String pvcQuantity;
  private final String pvcName;
  private final String pvcAccessMode;
  private final PVCSubPathHelper pvcSubPathHelper;
  private final KubernetesNamespaceFactory factory;

  @Inject
  public CommonPVCStrategy(
      @Named("che.infra.kubernetes.pvc.name") String pvcName,
      @Named("che.infra.kubernetes.pvc.quantity") String pvcQuantity,
      @Named("che.infra.kubernetes.pvc.access_mode") String pvcAccessMode,
      @Named("che.infra.kubernetes.pvc.precreate_subpaths") boolean preCreateDirs,
      PVCSubPathHelper pvcSubPathHelper,
      KubernetesNamespaceFactory factory) {
    this.pvcName = pvcName;
    this.pvcQuantity = pvcQuantity;
    this.pvcAccessMode = pvcAccessMode;
    this.preCreateDirs = preCreateDirs;
    this.pvcSubPathHelper = pvcSubPathHelper;
    this.factory = factory;
  }

  @Override
  public void provision(KubernetesEnvironment k8sEnv, RuntimeIdentity identity)
      throws InfrastructureException {
    final String workspaceId = identity.getWorkspaceId();
    final Set<String> subPaths = new HashSet<>();
    final PersistentVolumeClaim pvc = newPVC(pvcName, pvcAccessMode, pvcQuantity);
    k8sEnv.getPersistentVolumeClaims().put(pvcName, pvc);
    for (Pod pod : k8sEnv.getPods().values()) {
      PodSpec podSpec = pod.getSpec();
      for (Container container : podSpec.getContainers()) {
        String machineName = Names.machineName(pod, container);
        InternalMachineConfig machineConfig = k8sEnv.getMachines().get(machineName);
        addMachineVolumes(workspaceId, subPaths, pod, container, machineConfig.getVolumes());
      }
    }
    if (preCreateDirs && !subPaths.isEmpty()) {
      pvc.setAdditionalProperty(
          format(SUBPATHS_PROPERTY_FMT, workspaceId),
          subPaths.toArray(new String[subPaths.size()]));
    }
  }

  @Override
  public void prepare(KubernetesEnvironment k8sEnv, String workspaceId)
      throws InfrastructureException {
    final Collection<PersistentVolumeClaim> claims = k8sEnv.getPersistentVolumeClaims().values();
    if (!claims.isEmpty()) {
      final KubernetesNamespace namespace = factory.create(workspaceId);
      final KubernetesPersistentVolumeClaims pvcs = namespace.persistentVolumeClaims();
      final Set<String> existing =
          pvcs.get().stream().map(p -> p.getMetadata().getName()).collect(toSet());
      for (PersistentVolumeClaim pvc : claims) {
        final String[] subpaths =
            (String[])
                pvc.getAdditionalProperties().remove(format(SUBPATHS_PROPERTY_FMT, workspaceId));
        if (!existing.contains(pvc.getMetadata().getName())) {
          pvcs.create(pvc);
        }
        if (preCreateDirs && subpaths != null) {
          pvcSubPathHelper.createDirs(workspaceId, subpaths);
        }
      }
    }
  }

  @Override
  public void cleanup(String workspaceId) throws InfrastructureException {
    pvcSubPathHelper.removeDirsAsync(workspaceId, getWorkspaceSubPath(workspaceId));
  }

  private void addMachineVolumes(
      String workspaceId,
      Set<String> subPaths,
      Pod pod,
      Container container,
      Map<String, Volume> volumes) {
    if (volumes.isEmpty()) {
      return;
    }
    for (Entry<String, Volume> volumeEntry : volumes.entrySet()) {
      String volumePath = volumeEntry.getValue().getPath();
      String subPath =
          getVolumeSubPath(workspaceId, volumeEntry.getKey(), Names.machineName(pod, container));
      subPaths.add(subPath);

      container.getVolumeMounts().add(newVolumeMount(pvcName, volumePath, subPath));
      addVolumeIfNeeded(pod.getSpec());
    }
  }

  private void addVolumeIfNeeded(PodSpec podSpec) {
    if (podSpec.getVolumes().stream().noneMatch(volume -> volume.getName().equals(pvcName))) {

      podSpec.getVolumes().add(newVolume(pvcName, pvcName));
    }
  }

  /** Get sub-path that holds all the volumes of a particular workspace */
  private String getWorkspaceSubPath(String workspaceId) {
    return workspaceId;
  }

  /** Get sub-path for particular volume in a particular workspace */
  private String getVolumeSubPath(String workspaceId, String volumeName, String machineName) {
    // logs must be located inside the folder related to the machine because few machines can
    // contain the identical agents and in this case, a conflict is possible.
    if (LOGS_VOLUME_NAME.equals(volumeName)) {
      return getWorkspaceSubPath(workspaceId) + '/' + volumeName + '/' + machineName;
    }
    // this path should correlate with path returned by method getWorkspaceSubPath
    // because this logic is used to correctly cleanup sub-paths related to a workspace
    return getWorkspaceSubPath(workspaceId) + '/' + volumeName;
  }
}
