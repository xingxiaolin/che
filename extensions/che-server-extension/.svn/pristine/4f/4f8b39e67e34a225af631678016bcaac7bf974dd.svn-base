/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.sgitg.cuap.wpc;

import org.eclipse.che.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.sgitg.cuap.wpc.FileOperationService;
import com.sgitg.cuap.wpc.ProjectService;

@DynaModule
public class MyGuiceModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
    	bind(GenerateCode.class);
    	
        bind(ProjectService.class);
        bind(FileOperationService.class);
//        
//        bind(ReadFormFileService.class);
//        bind(ReadJarFormFileService.class);
//        bind(SaveFormFileService.class);  
    }
}