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
package org.eclipse.che.workspace.infrastructure.openshift.project;

import com.google.common.collect.ImmutableMap;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * Helps to work with OpenShift objects.
 *
 * @author Anton Korneta
 */
public class OpenShiftObjectUtil {

  private static final String STORAGE_PARAM = "storage";

  /** Adds label to target OpenShift object. */
  public static void putLabel(HasMetadata target, String key, String value) {
    ObjectMeta metadata = target.getMetadata();

    if (metadata == null) {
      target.setMetadata(metadata = new ObjectMeta());
    }

    Map<String, String> labels = metadata.getLabels();
    if (labels == null) {
      metadata.setLabels(labels = new HashMap<>());
    }

    labels.put(key, value);
  }

  /** Adds selector into target OpenShift service. */
  public static void putSelector(Service target, String key, String value) {
    ServiceSpec spec = target.getSpec();

    if (spec == null) {
      target.setSpec(spec = new ServiceSpec());
    }

    Map<String, String> selector = spec.getSelector();
    if (selector == null) {
      spec.setSelector(selector = new HashMap<>());
    }

    selector.put(key, value);
  }

  /**
   * Returns new instance of {@link PersistentVolumeClaim} with specified name, accessMode and
   * quantity.
   */
  public static PersistentVolumeClaim newPVC(String name, String accessMode, String quantity) {
    return new PersistentVolumeClaimBuilder()
        .withNewMetadata()
        .withName(name)
        .endMetadata()
        .withNewSpec()
        .withAccessModes(accessMode)
        .withNewResources()
        .withRequests(ImmutableMap.of(STORAGE_PARAM, new Quantity(quantity)))
        .endResources()
        .endSpec()
        .build();
  }

  /** Returns new instance of {@link VolumeMount} with specified name, mountPath and subPath. */
  public static VolumeMount newVolumeMount(String name, String mountPath, String subPath) {
    return new VolumeMountBuilder()
        .withMountPath(mountPath)
        .withName(name)
        .withSubPath(subPath)
        .build();
  }

  /** Returns new instance of {@link Volume} with specified name and name of claim related to. */
  public static Volume newVolume(String name, String pvcName) {
    final PersistentVolumeClaimVolumeSource pvcs =
        new PersistentVolumeClaimVolumeSourceBuilder().withClaimName(pvcName).build();
    return new VolumeBuilder().withPersistentVolumeClaim(pvcs).withName(name).build();
  }
}
