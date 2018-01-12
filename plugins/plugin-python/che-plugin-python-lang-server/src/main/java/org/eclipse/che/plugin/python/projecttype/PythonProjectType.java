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
package org.eclipse.che.plugin.python.projecttype;

import static org.eclipse.che.plugin.python.shared.ProjectAttributes.LANGUAGE;
import static org.eclipse.che.plugin.python.shared.ProjectAttributes.PYTHON_ID;
import static org.eclipse.che.plugin.python.shared.ProjectAttributes.PYTHON_NAME;

import com.google.inject.Inject;
import org.eclipse.che.api.project.server.type.ProjectTypeDef;

/**
 * Python project type.
 *
 * @author Valeriy Svydenko
 */
public class PythonProjectType extends ProjectTypeDef {
  @Inject
  public PythonProjectType() {
    super(PYTHON_ID, PYTHON_NAME, true, false, true);
    addConstantDefinition(LANGUAGE, LANGUAGE, PYTHON_ID);
  }
}
