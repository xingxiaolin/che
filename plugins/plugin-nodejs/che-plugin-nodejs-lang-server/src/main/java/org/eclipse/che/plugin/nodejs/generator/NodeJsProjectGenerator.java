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
package org.eclipse.che.plugin.nodejs.generator;

import static org.eclipse.che.api.fs.server.WsPathUtils.resolve;

import com.google.inject.Inject;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.fs.server.FsManager;
import org.eclipse.che.api.project.server.handlers.CreateProjectHandler;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.plugin.nodejs.shared.Constants;

/**
 * Generates new project which contains file with default content.
 *
 * @author Dmitry Shnurenko
 */
public class NodeJsProjectGenerator implements CreateProjectHandler {

  private final FsManager fsManager;

  @Inject
  public NodeJsProjectGenerator(FsManager fsManager) {
    this.fsManager = fsManager;
  }

  @Override
  public void onCreateProject(
      String projectWsPath, Map<String, AttributeValue> attributes, Map<String, String> options)
      throws ForbiddenException, ConflictException, ServerException, NotFoundException {

    fsManager.createDir(projectWsPath);
    InputStream inputStream =
        getClass().getClassLoader().getResourceAsStream("files/default_node_content");
    String wsPath = resolve(projectWsPath, "hello.js");
    fsManager.createFile(wsPath, inputStream);
  }

  @Override
  public String getProjectType() {
    return Constants.NODE_JS_PROJECT_TYPE_ID;
  }
}
