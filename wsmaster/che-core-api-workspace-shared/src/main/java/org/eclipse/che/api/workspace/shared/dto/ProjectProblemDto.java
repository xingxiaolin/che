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

import org.eclipse.che.api.core.model.project.ProjectProblem;
import org.eclipse.che.dto.shared.DTO;

/** @author Sergii Kabashniuk */
@DTO
public interface ProjectProblemDto extends ProjectProblem {

  int getCode();

  void setCode(int status);

  ProjectProblemDto withCode(int status);

  String getMessage();

  void setMessage(String message);

  ProjectProblemDto withMessage(String message);
}
