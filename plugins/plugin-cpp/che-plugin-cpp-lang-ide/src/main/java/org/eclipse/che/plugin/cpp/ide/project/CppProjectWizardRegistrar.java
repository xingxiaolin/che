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
package org.eclipse.che.plugin.cpp.ide.project;

import static org.eclipse.che.plugin.cpp.shared.Constants.CPP_PROJECT_TYPE_ID;

import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.ide.api.wizard.WizardPage;
import org.eclipse.che.plugin.cpp.ide.CppExtension;

/**
 * Provides information for registering C_PROJECT_TYPE_ID++ project type into project wizard.
 *
 * @author Vitalii Parfonov
 */
public class CppProjectWizardRegistrar implements ProjectWizardRegistrar {

  private final List<Provider<? extends WizardPage<MutableProjectConfig>>> wizardPages;

  public CppProjectWizardRegistrar() {
    wizardPages = new ArrayList<>();
  }

  @NotNull
  public String getProjectTypeId() {
    return CPP_PROJECT_TYPE_ID;
  }

  @NotNull
  public String getCategory() {
    return CppExtension.C_CATEGORY;
  }

  @NotNull
  public List<Provider<? extends WizardPage<MutableProjectConfig>>> getWizardPages() {
    return wizardPages;
  }
}
