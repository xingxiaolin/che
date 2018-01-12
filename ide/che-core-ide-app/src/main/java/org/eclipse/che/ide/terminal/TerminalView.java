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
package org.eclipse.che.ide.terminal;

import com.google.inject.ImplementedBy;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.mvp.View;

/**
 * The interface defines methods to control displaying of terminal.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TerminalViewImpl.class)
interface TerminalView extends View<TerminalView.ActionDelegate> {

  interface ActionDelegate {

    /**
     * Set terminal size
     *
     * @param x amount of terminal columns
     * @param y amount of terminal rows
     */
    void setTerminalSize(int x, int y);
  }

  /**
   * Change visibility state of panel.
   *
   * @param visible <code>true</code> panel is visible,<code>false</code> panel is not visible
   */
  void setVisible(boolean visible);

  /**
   * Opens current terminal.
   *
   * @param terminal terminal which will be opened
   */
  void openTerminal(@NotNull TerminalJso terminal);

  /**
   * Shows special error message when terminal is failed.
   *
   * @param message message which will be shown
   */
  void showErrorMessage(@NotNull String message);
}
