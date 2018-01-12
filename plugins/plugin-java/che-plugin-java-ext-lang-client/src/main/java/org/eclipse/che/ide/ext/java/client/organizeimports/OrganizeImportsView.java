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
package org.eclipse.che.ide.ext.java.client.organizeimports;

import com.google.inject.ImplementedBy;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.java.shared.dto.ConflictImportDTO;

/**
 * The visual part of Organize Imports wizard that has an ability to resolve import conflicts.
 *
 * @author Valeriy Svydenko
 */
@ImplementedBy(OrganizeImportsViewImpl.class)
interface OrganizeImportsView extends View<OrganizeImportsView.ActionDelegate> {

  /** Show Organize Imports panel with the list of conflict imports. */
  void show(ConflictImportDTO match);

  /** Hide Organize Imports panel. */
  void hide();

  /** @return selected import for current view page. */
  String getSelectedImport();

  /**
   * Selects an import into current view page.
   *
   * @param fqn import which need to select
   */
  void setSelectedImport(String fqn);

  /**
   * Show new view page for choosing necessary import.
   *
   * @param match conflict object with possible options to choose
   */
  void changePage(ConflictImportDTO match);

  /**
   * Setts enable scope of the Finish button.
   *
   * @param isEnable enable state of scope property
   */
  void setEnableFinishButton(boolean isEnable);

  /**
   * Setts enable scope of the Next button.
   *
   * @param isEnable enable state of scope property
   */
  void setEnableNextButton(boolean isEnable);

  /**
   * Setts enable scope of the Back button.
   *
   * @param isEnable enable state of scope property
   */
  void setEnableBackButton(boolean isEnable);

  interface ActionDelegate {
    /** Performs some actions in response to user's clicking on the 'Next' button. */
    void onNextButtonClicked();

    /** Performs some actions in response to user's clicking on the 'Back' button. */
    void onBackButtonClicked();

    /** Performs some actions in response to user's clicking on the 'Finish' button. */
    void onFinishButtonClicked();

    /** Performs some actions in response to user's clicking on the 'Cancel' button. */
    void onCancelButtonClicked();
  }
}
