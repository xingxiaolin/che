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
package org.eclipse.che.ide.ui.loaders;

/**
 * Loader interface
 *
 * @author Vitaliy Guliy
 */
public interface PopupLoader {

  /** Marks operation successful. */
  void setSuccess();

  /** Marks operation failed. */
  void setError();

  /** Shows a button to download logs. */
  void showDownloadButton();

  /**
   * Sets an action delegate to handle user actions.
   *
   * @param actionDelegate action delegate
   */
  void setDelegate(ActionDelegate actionDelegate);

  interface ActionDelegate {

    void onDownloadLogs();
  }
}
