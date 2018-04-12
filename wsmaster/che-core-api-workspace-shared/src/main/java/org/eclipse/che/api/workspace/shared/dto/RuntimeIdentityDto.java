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
package org.eclipse.che.api.workspace.shared.dto;

import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.dto.shared.DTO;

/** @author gazarenkov */
@DTO
public interface RuntimeIdentityDto extends RuntimeIdentity {

  @Override
  String getWorkspaceId();

  RuntimeIdentityDto withWorkspaceId(String workspaceId);

  @Override
  String getEnvName();

  RuntimeIdentityDto withEnvName(String envName);

  @Override
  String getOwnerId();

  RuntimeIdentityDto withOwnerId(String ownerId);
}
