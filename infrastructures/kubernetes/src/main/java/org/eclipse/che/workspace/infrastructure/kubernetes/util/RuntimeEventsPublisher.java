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
package org.eclipse.che.workspace.infrastructure.kubernetes.util;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.model.workspace.runtime.MachineStatus;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.core.model.workspace.runtime.Server;
import org.eclipse.che.api.core.model.workspace.runtime.ServerStatus;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.workspace.server.DtoConverter;
import org.eclipse.che.api.workspace.shared.dto.event.MachineLogEvent;
import org.eclipse.che.api.workspace.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.api.workspace.shared.dto.event.RuntimeStatusEvent;
import org.eclipse.che.api.workspace.shared.dto.event.ServerStatusEvent;
import org.eclipse.che.dto.server.DtoFactory;

/** @author Anton Korneta */
@Singleton
public class RuntimeEventsPublisher {

  private static final String RUNTIME_STOPPED_STATE = "STOPPED";
  private static final String RUNTIME_RUNNING_STATE = "RUNNING";

  private final EventService eventService;

  @Inject
  public RuntimeEventsPublisher(EventService eventService) {
    this.eventService = eventService;
  }

  public void sendStartingEvent(String machineName, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(MachineStatusEvent.class)
            .withIdentity(DtoConverter.asDto(runtimeId))
            .withEventType(MachineStatus.STARTING)
            .withMachineName(machineName));
  }

  public void sendRunningEvent(String machineName, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(MachineStatusEvent.class)
            .withIdentity(DtoConverter.asDto(runtimeId))
            .withEventType(MachineStatus.RUNNING)
            .withMachineName(machineName));
  }

  public void sendFailedEvent(String machineName, String message, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(MachineStatusEvent.class)
            .withIdentity(DtoConverter.asDto(runtimeId))
            .withEventType(MachineStatus.FAILED)
            .withMachineName(machineName)
            .withError(message));
  }

  public void sendRuntimeStoppedEvent(String errorMsg, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(RuntimeStatusEvent.class)
            .withIdentity(DtoConverter.asDto(runtimeId))
            .withStatus(RUNTIME_STOPPED_STATE)
            .withPrevStatus(RUNTIME_RUNNING_STATE)
            .withFailed(true)
            .withError(errorMsg));
  }

  public void sendServerStatusEvent(
      String machineName, String serverName, Server server, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(ServerStatusEvent.class)
            .withIdentity(DtoConverter.asDto(runtimeId))
            .withMachineName(machineName)
            .withServerName(serverName)
            .withStatus(server.getStatus())
            .withServerUrl(server.getUrl()));
  }

  public void sendServerRunningEvent(
      String machineName, String serverName, String serverUrl, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(ServerStatusEvent.class)
            .withIdentity(DtoConverter.asDto(runtimeId))
            .withMachineName(machineName)
            .withServerName(serverName)
            .withStatus(ServerStatus.RUNNING)
            .withServerUrl(serverUrl));
  }

  public void sendMachineLogEnvent(
      String machineName, String text, String time, RuntimeIdentity runtimeId) {
    eventService.publish(
        DtoFactory.newDto(MachineLogEvent.class)
            .withMachineName(machineName)
            .withRuntimeId(DtoConverter.asDto(runtimeId))
            .withText(text)
            .withTime(time));
  }
}
