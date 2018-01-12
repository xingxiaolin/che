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
package org.eclipse.che.plugin.cpp.ide.inject;

import static org.eclipse.che.plugin.cpp.shared.Constants.CPP_EXT;
import static org.eclipse.che.plugin.cpp.shared.Constants.C_EXT;
import static org.eclipse.che.plugin.cpp.shared.Constants.H_EXT;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.plugin.cpp.ide.CppResources;
import org.eclipse.che.plugin.cpp.ide.project.CProjectWizardRegistrar;
import org.eclipse.che.plugin.cpp.ide.project.CppProjectWizardRegistrar;

/** @author Vitalii Parfonov */
@ExtensionGinModule
public class CppGinModule extends AbstractGinModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class)
        .addBinding()
        .to(CppProjectWizardRegistrar.class);
    GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class)
        .addBinding()
        .to(CProjectWizardRegistrar.class);
  }

  @Provides
  @Singleton
  @Named("CFileType")
  protected FileType provideCFile() {
    return new FileType(CppResources.INSTANCE.cFile(), C_EXT);
  }

  @Provides
  @Singleton
  @Named("CppFileType")
  protected FileType provideCppFile() {
    return new FileType(CppResources.INSTANCE.cppFile(), CPP_EXT);
  }

  @Provides
  @Singleton
  @Named("HFileType")
  protected FileType provideHeaderFile() {
    return new FileType(CppResources.INSTANCE.cHeaderFile(), H_EXT);
  }
}
