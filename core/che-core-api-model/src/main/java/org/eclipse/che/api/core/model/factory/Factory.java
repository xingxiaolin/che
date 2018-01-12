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
package org.eclipse.che.api.core.model.factory;

import org.eclipse.che.api.core.model.workspace.WorkspaceConfig;

/**
 * Defines the contract for the factory instance.
 *
 * @author Anton Korneta
 */
public interface Factory {

  /** Returns the identifier of this factory instance, it is mandatory and unique. */
  String getId();

  /** Returns the version of this factory instance, it is mandatory. */
  String getV();

  /** Returns a name of this factory instance, the name is unique for creator. */
  String getName();

  /** Returns creator of this factory instance. */
  Author getCreator();

  /**
   * Returns a workspace configuration of this factory instance, it is mandatory for every factory
   * instance.
   */
  WorkspaceConfig getWorkspace();

  /** Returns restrictions of this factory instance. */
  Policies getPolicies();

  /** Returns factory button for this instance. */
  Button getButton();

  /** Returns IDE for this factory instance. */
  Ide getIde();
}
