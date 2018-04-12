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
package org.eclipse.che.ide.part.editor.recent;

import com.google.inject.ImplementedBy;
import java.util.List;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.resources.tree.FileNode;

/**
 * View for the {@link OpenRecentFilesPresenter}.
 *
 * @author Vlad Zhukovskiy
 */
@ImplementedBy(OpenRecentFileViewImpl.class)
public interface OpenRecentFilesView extends View<OpenRecentFilesView.ActionDelegate> {

  /**
   * Set recent file list.
   *
   * @param recentFiles recent file list
   */
  void setRecentFiles(List<FileNode> recentFiles);

  /** Clear recent file list. */
  void clearRecentFiles();

  /** Show dialog. */
  void showDialog();

  interface ActionDelegate {}
}
