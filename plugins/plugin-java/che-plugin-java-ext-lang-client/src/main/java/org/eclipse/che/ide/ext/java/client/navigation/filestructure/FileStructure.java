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
package org.eclipse.che.ide.ext.java.client.navigation.filestructure;

import com.google.inject.ImplementedBy;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.java.shared.dto.model.CompilationUnit;
import org.eclipse.che.ide.ext.java.shared.dto.model.Member;

/**
 * The visual part of window which contains file structure.
 *
 * @author Valeriy Svydenko
 */
@ImplementedBy(FileStructureImpl.class)
interface FileStructure extends View<FileStructure.ActionDelegate> {

  /**
   * Set a title of the navigation window.
   *
   * @param title new window's title
   */
  void setTitle(String title);

  /**
   * Show structure of the opened class.
   *
   * @param compilationUnit compilation unit of the current source file
   * @param showInheritedMembers <code>true</code> iff inherited members are shown
   */
  void setStructure(CompilationUnit compilationUnit, boolean showInheritedMembers);

  /** Closes window. */
  void close();

  /** Show dialog. */
  void show();

  interface ActionDelegate {
    /**
     * Closes window and select a region of the active element in the editor.
     *
     * @param member selected member
     */
    void actionPerformed(Member member);

    /**
     * Performs some actions(e.c. set cursor in previous position in active editor) when user click
     * on escape button to close file structure dialog.
     */
    void onEscapeClicked();
  }
}
