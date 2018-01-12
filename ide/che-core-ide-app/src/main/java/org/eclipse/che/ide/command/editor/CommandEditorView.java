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
package org.eclipse.che.ide.command.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.eclipse.che.ide.api.mvp.View;

/**
 * The view for {@link CommandEditor}.
 *
 * @author Artem Zatsarynnyi
 */
public interface CommandEditorView extends View<CommandEditorView.ActionDelegate> {

  /**
   * Add page to the view. New page will be added to the top.
   *
   * @param page page to add
   * @param title text that should be used as page's title
   */
  void addPage(IsWidget page, String title);

  /**
   * Set whether saving command is enabled or not.
   *
   * @param enable {@code true} if command saving is enabled and {@code false} otherwise
   */
  void setSaveEnabled(boolean enable);

  /** The action delegate for this view. */
  interface ActionDelegate {

    /** Called when reverting command changes is requested. */
    void onCommandCancel();

    /** Called when saving command is requested. */
    void onCommandSave();
  }
}
