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
package org.eclipse.che.plugin.languageserver.ide.quickopen;

import com.google.inject.ImplementedBy;
import org.eclipse.che.ide.api.mvp.View;

/** @author Evgen Vidolob */
@ImplementedBy(QuickOpenViewImpl.class)
public interface QuickOpenView extends View<QuickOpenView.ActionDelegate> {

  void focusOnInput();

  void show(String value);

  void hide();

  void setModel(QuickOpenModel model);

  interface ActionDelegate {

    void valueChanged(String value);

    void onClose(boolean canceled);
  }
}
