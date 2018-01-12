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
package org.eclipse.che.ide.ext.java.client.newsourcefile;

import java.util.List;
import org.eclipse.che.ide.api.mvp.View;

/**
 * The view of {@link NewJavaSourceFilePresenter}.
 *
 * @author Artem Zatsarynnyi
 */
public interface NewJavaSourceFileView extends View<NewJavaSourceFileView.ActionDelegate> {

  /** Set available Java source file types. */
  void setTypes(List<JavaSourceFileType> types);

  /** Returns content of the name field. */
  String getName();

  /** Returns selected source file type. */
  JavaSourceFileType getSelectedType();

  /** Show dialog. */
  void showDialog();

  /** Close dialog. */
  void close();

  void showErrorHint(String text);

  void hideErrorHint();

  public interface ActionDelegate {
    /** Performs any actions appropriate in response to the user having pressed the Ok button. */
    void onOkClicked();

    /**
     * Performs any actions appropriate in response to the user having pressed the Cancel button.
     */
    void onCancelClicked();

    /** Called when name changed. */
    void onNameChanged();
  }
}
