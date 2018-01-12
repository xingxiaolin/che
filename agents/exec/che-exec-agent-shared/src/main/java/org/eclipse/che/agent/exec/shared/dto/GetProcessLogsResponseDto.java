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
package org.eclipse.che.agent.exec.shared.dto;

import org.eclipse.che.dto.shared.DTO;

/** @author Dmitry Kuleshov */
@DTO
public interface GetProcessLogsResponseDto {
  String getKind();

  GetProcessLogsResponseDto withKind(String kind);

  String getTime();

  GetProcessLogsResponseDto withTime(String time);

  String getText();

  GetProcessLogsResponseDto withText(String text);
}
