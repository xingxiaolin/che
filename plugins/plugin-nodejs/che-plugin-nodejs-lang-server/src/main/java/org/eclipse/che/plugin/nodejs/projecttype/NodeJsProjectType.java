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
package org.eclipse.che.plugin.nodejs.projecttype;

import static org.eclipse.che.plugin.nodejs.shared.Constants.LANGUAGE;
import static org.eclipse.che.plugin.nodejs.shared.Constants.NODE_JS_PROJECT_TYPE_ID;
import static org.eclipse.che.plugin.nodejs.shared.Constants.RUN_PARAMETERS_ATTRIBUTE;

import org.eclipse.che.api.project.server.type.ProjectTypeDef;

/** @author Dmitry Shnurenko */
public class NodeJsProjectType extends ProjectTypeDef {

  public NodeJsProjectType() {
    super(NODE_JS_PROJECT_TYPE_ID, "Node JS", true, false, true);
    addConstantDefinition(LANGUAGE, LANGUAGE, NODE_JS_PROJECT_TYPE_ID);
    addVariableDefinition(RUN_PARAMETERS_ATTRIBUTE, "Run parameters", false);
  }
}
