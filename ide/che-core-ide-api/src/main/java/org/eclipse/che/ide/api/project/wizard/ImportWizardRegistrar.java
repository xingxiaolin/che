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
package org.eclipse.che.ide.api.project.wizard;

import com.google.inject.Provider;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.wizard.WizardPage;

/**
 * Defines the requirements for an object that provides an information for registering project
 * importer into project import wizard.
 *
 * <p>Implementations of this interface need to be registered using a multibinder in order to be
 * picked up by project wizard.
 *
 * @author Artem Zatsarynnyi
 */
public interface ImportWizardRegistrar {

  /** Returns ID of the project importer that should be registered in project import wizard. */
  @NotNull
  String getImporterId();

  /** Returns pages that should be used in project import wizard. */
  @NotNull
  List<Provider<? extends WizardPage<MutableProjectConfig>>> getWizardPages();
}
