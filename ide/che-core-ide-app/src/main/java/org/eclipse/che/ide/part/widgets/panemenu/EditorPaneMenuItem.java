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
package org.eclipse.che.ide.part.widgets.panemenu;

import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.mvp.View;

/**
 * Contract to implementations of item for displaying in {@link EditorPaneMenu}
 *
 * @author Dmitry Shnurenko
 * @author Vitaliy Guliy
 */
public interface EditorPaneMenuItem<T> extends View<EditorPaneMenuItem.ActionDelegate<T>> {

  /** Returns associated data. */
  T getData();

  interface ActionDelegate<T> {

    /** Handle clicking on item */
    void onItemClicked(@NotNull EditorPaneMenuItem<T> item);

    /**
     * Handle clicking on close button
     *
     * @param item item to close
     */
    void onCloseButtonClicked(@NotNull EditorPaneMenuItem<T> item);
  }
}
