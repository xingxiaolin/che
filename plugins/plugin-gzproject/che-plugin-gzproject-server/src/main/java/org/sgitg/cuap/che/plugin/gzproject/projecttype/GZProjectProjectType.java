/*******************************************************************************
 * Copyright (c) 2016 Rogue Wave Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rogue Wave Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.sgitg.cuap.che.plugin.gzproject.projecttype;

import com.google.inject.Inject;
import org.eclipse.che.api.project.server.type.ProjectTypeDef;
import org.sgitg.cuap.che.plugin.gzproject.shared.Constants;
import static org.sgitg.cuap.che.plugin.gzproject.shared.Constants.GZPROJECT_PROJECT_TYPE_ID;

/**
 * GZProject project type.
 * 
 * @author Kaloyan Raev
 */
public class GZProjectProjectType extends ProjectTypeDef {
    @Inject
    public GZProjectProjectType() {
        super(GZPROJECT_PROJECT_TYPE_ID, "gzproject", true, false);
        addConstantDefinition(Constants.LANGUAGE, "language", "java");
    }
}
