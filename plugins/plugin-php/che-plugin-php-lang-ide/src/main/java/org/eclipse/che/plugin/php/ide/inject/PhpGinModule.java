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
package org.eclipse.che.plugin.php.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.plugin.php.ide.PhpResources;
import org.eclipse.che.plugin.php.ide.project.PhpProjectWizardRegistrar;
import org.eclipse.che.plugin.php.shared.Constants;

/** @author Kaloyan Raev */
@ExtensionGinModule
public class PhpGinModule extends AbstractGinModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class)
        .addBinding()
        .to(PhpProjectWizardRegistrar.class);
  }

  @Provides
  @Singleton
  @Named("PhpFileType")
  protected FileType provideCppFile() {
    return new FileType(PhpResources.INSTANCE.phpFile(), Constants.PHP_EXT);
  }
}
