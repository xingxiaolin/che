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
package org.eclipse.che.agent.exec.shared.dto.event;

import org.eclipse.che.agent.exec.shared.dto.DtoWithPid;
import org.eclipse.che.dto.shared.DTO;

@DTO
public interface ProcessStartedEventDto extends DtoWithPid {
  ProcessStartedEventDto withPid(int pid);

  String getTime();

  ProcessStartedEventDto withTime(String time);

  int getNativePid();

  ProcessStartedEventDto withNativePid(int nativePid);

  String getName();

  ProcessStartedEventDto withName(String name);

  String getCommandLine();

  ProcessStartedEventDto withCommandLine(String commandLine);
}
