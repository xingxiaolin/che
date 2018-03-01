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
package org.sgitg.cuap.che.plugin.gzproject.inject;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.eclipse.che.api.project.server.type.ProjectTypeDef;
import org.sgitg.cuap.che.plugin.gzproject.projecttype.GZProjectProjectType;
import org.eclipse.che.inject.DynaModule;
import org.eclipse.che.api.project.server.handlers.ProjectHandler;
import static com.google.inject.multibindings.Multibinder.newSetBinder;
import org.sgitg.cuap.che.plugin.gzproject.generator.GZProjectProjectGenerator;
/**
 * The module that contains configuration of the server side part of the
 * GZProject extension.
 *
 * @author Kaloyan Raev
 */
@DynaModule
public class GZProjectModule extends AbstractModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
    	Multibinder<ProjectTypeDef> projectTypeMultibinder = Multibinder.newSetBinder(binder(), ProjectTypeDef.class);
        projectTypeMultibinder.addBinding().to(GZProjectProjectType.class);
        Multibinder<ProjectHandler> projectHandlerMultibinder = newSetBinder(binder(), ProjectHandler.class);
        projectHandlerMultibinder.addBinding().to(GZProjectProjectGenerator.class);
    }
}
