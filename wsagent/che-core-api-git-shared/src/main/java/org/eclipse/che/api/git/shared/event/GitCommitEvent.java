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
package org.eclipse.che.api.git.shared.event;

import java.util.List;
import java.util.Map;
import org.eclipse.che.api.git.shared.EditedRegion;
import org.eclipse.che.api.git.shared.Status;
import org.eclipse.che.dto.shared.DTO;

/**
 * Event for indicating that Git commit operation is executed.
 *
 * @author Igor Vinokur.
 */
@DTO
public interface GitCommitEvent extends GitEvent {

  /** Returns Git status */
  Status getStatus();

  void setStatus(Status status);

  GitCommitEvent withStatus(Status status);

  /** Map of modified files and their edited regions. */
  Map<String, List<EditedRegion>> getModifiedFiles();

  void setModifiedFiles(Map<String, List<EditedRegion>> modifiedFiles);

  GitCommitEvent withModifiedFiles(Map<String, List<EditedRegion>> modifiedFiles);

  @Override
  String getProjectName();

  void setProjectName(String projectName);

  GitCommitEvent withProjectName(String projectName);
}
