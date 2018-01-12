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

@DTO
public interface ProcessStartResponseDto extends DtoWithPid {
  ProcessStartResponseDto withPid(int pid);

  String getName();

  ProcessStartResponseDto withName(String name);

  String getCommandLine();

  ProcessStartResponseDto withCommandLine(String commandLine);

  String getType();

  ProcessStartResponseDto withType(String type);

  boolean getAlive();

  ProcessStartResponseDto withAlive(boolean alive);

  int getNativePid();

  ProcessStartResponseDto withNativePid(int nativePid);

  int getExitCode();

  ProcessStartResponseDto withExitCode(int exitCode);
}
