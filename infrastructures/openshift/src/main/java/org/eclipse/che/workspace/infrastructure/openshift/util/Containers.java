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
package org.eclipse.che.workspace.infrastructure.openshift.util;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;

/** @author Anton Korneta */
public class Containers {

  public static long getRamLimit(Container container) {
    final ResourceRequirements resources = container.getResources();
    final Quantity quantity;
    if (resources != null
        && resources.getLimits() != null
        && (quantity = resources.getLimits().get("memory")) != null
        && quantity.getAmount() != null) {
      return KubernetesSize.toBytes(quantity.getAmount());
    }
    return 0;
  }

  public static void addRamLimit(Container container, long bytes) {
    final ResourceRequirementsBuilder resourceBuilder =
        container.getResources() != null
            ? new ResourceRequirementsBuilder(container.getResources())
            : new ResourceRequirementsBuilder();
    container.setResources(
        resourceBuilder.addToLimits("memory", new Quantity(String.valueOf(bytes))).build());
  }
}
