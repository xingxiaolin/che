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
package org.eclipse.che.workspace.infrastructure.kubernetes;

/**
 * Constants for Kubernetes implementation of spi.
 *
 * @author Sergii Leshchenko
 */
public final class Constants {

  /**
   * The label that contains a value with original object name.
   *
   * <p>Names of Kubernetes objects should be modified to avoid collision with objects of other
   * workspaces.
   *
   * @see org.eclipse.che.workspace.infrastructure.kubernetes.provision.UniqueNamesProvisioner
   */
  public static final String CHE_ORIGINAL_NAME_LABEL = "che.original_name";

  /** The label that contains a value with workspace id to which object belongs to. */
  public static final String CHE_WORKSPACE_ID_LABEL = "che.workspace_id";

  /** The label that contains a value with volume name. */
  public static final String CHE_VOLUME_NAME_LABEL = "che.workspace.volume_name";

  /**
   * The annotation with the wildcard reserved for container name. Formatted annotation with the
   * real container name is used to get machine name.
   */
  public static final String MACHINE_NAME_ANNOTATION_FMT =
      "org.eclipse.che.container.%s.machine_name";

  private Constants() {}
}
