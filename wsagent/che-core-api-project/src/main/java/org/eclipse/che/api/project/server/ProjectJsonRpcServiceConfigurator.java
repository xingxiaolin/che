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
package org.eclipse.che.api.project.server;

import static org.eclipse.che.api.project.shared.Constants.Services.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerConfigurator;
import org.eclipse.che.api.project.server.impl.ProjectJsonRpcServiceBackEnd;
import org.eclipse.che.api.project.shared.dto.service.*;

@Singleton
public class ProjectJsonRpcServiceConfigurator {

  private final RequestHandlerConfigurator handlers;
  private final ProjectJsonRpcServiceBackEnd service;

  @Inject
  public ProjectJsonRpcServiceConfigurator(
      RequestHandlerConfigurator handlers, ProjectJsonRpcServiceBackEnd service) {
    this.handlers = handlers;
    this.service = service;
  }

  @PostConstruct
  private void configure() {
    handlers
        .newConfiguration()
        .methodName(PROJECT_GET)
        .paramsAsDto(GetRequestDto.class)
        .resultAsDto(GetResponseDto.class)
        .withFunction(service::get);

    handlers
        .newConfiguration()
        .methodName(PROJECT_CREATE)
        .paramsAsDto(CreateRequestDto.class)
        .resultAsDto(CreateResponseDto.class)
        .withFunction(service::create);

    handlers
        .newConfiguration()
        .methodName(PROJECT_UPDATE)
        .paramsAsDto(UpdateRequestDto.class)
        .resultAsDto(UpdateResponseDto.class)
        .withFunction(service::update);

    handlers
        .newConfiguration()
        .methodName(PROJECT_DELETE)
        .paramsAsDto(DeleteRequestDto.class)
        .resultAsDto(DeleteResponseDto.class)
        .withFunction(service::delete);

    handlers
        .newConfiguration()
        .methodName(PROJECT_RECOGNIZE)
        .paramsAsDto(RecognizeRequestDto.class)
        .resultAsDto(RecognizeResponseDto.class)
        .withFunction(service::recognize);

    handlers
        .newConfiguration()
        .methodName(PROJECT_VERIFY)
        .paramsAsDto(VerifyRequestDto.class)
        .resultAsDto(VerifyResponseDto.class)
        .withFunction(service::verify);

    handlers
        .newConfiguration()
        .methodName(PROJECT_IMPORT)
        .paramsAsDto(ImportRequestDto.class)
        .resultAsDto(ImportResponseDto.class)
        .withBiFunction(service::doImport);
  }
}
