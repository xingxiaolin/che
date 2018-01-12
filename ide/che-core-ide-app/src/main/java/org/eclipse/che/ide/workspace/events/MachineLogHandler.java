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
package org.eclipse.che.ide.workspace.events;

import static org.eclipse.che.api.workspace.shared.Constants.MACHINE_LOG_METHOD;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerConfigurator;
import org.eclipse.che.api.workspace.shared.dto.event.MachineLogEvent;
import org.eclipse.che.ide.processes.panel.EnvironmentOutputEvent;

@Singleton
class MachineLogHandler {

  @Inject
  MachineLogHandler(RequestHandlerConfigurator configurator, EventBus eventBus) {
    configurator
        .newConfiguration()
        .methodName(MACHINE_LOG_METHOD)
        .paramsAsDto(MachineLogEvent.class)
        .noResult()
        .withBiConsumer(
            (endpointId, log) ->
                eventBus.fireEvent(
                    new EnvironmentOutputEvent(log.getText(), log.getMachineName())));
  }
}
