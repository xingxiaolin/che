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
package org.eclipse.che.api.debug.shared.dto;

import java.util.List;
import org.eclipse.che.api.debug.shared.model.ThreadState;
import org.eclipse.che.api.debug.shared.model.ThreadStatus;
import org.eclipse.che.dto.shared.DTO;

/** @author Anatolii Bazko */
@DTO
public interface ThreadStateDto extends ThreadState {

  @Override
  long getId();

  void setId(long id);

  ThreadStateDto withId(long id);

  @Override
  String getName();

  void setName(String name);

  ThreadStateDto withName(String name);

  @Override
  String getGroupName();

  void setGroupName(String groupName);

  ThreadStateDto withGroupName(String groupName);

  @Override
  List<StackFrameDumpDto> getFrames();

  void setFrames(List<StackFrameDumpDto> frames);

  ThreadStateDto withFrames(List<StackFrameDumpDto> frames);

  @Override
  ThreadStatus getStatus();

  void setStatus(ThreadStatus status);

  ThreadStateDto withStatus(ThreadStatus status);

  @Override
  boolean isSuspended();

  void setSuspended(boolean suspended);

  ThreadStateDto withSuspended(boolean suspended);
}
