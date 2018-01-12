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
package org.eclipse.che.ide.ext.java.client.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.navigation.filestructure.FileStructurePresenter;

/**
 * Action for open file structure window.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class FileStructureAction extends JavaEditorAction {

  private final FileStructurePresenter fileStructurePresenter;
  private final EditorAgent editorAgent;

  @Inject
  public FileStructureAction(
      FileStructurePresenter fileStructurePresenter,
      JavaLocalizationConstant locale,
      EditorAgent editorAgent,
      JavaResources resources,
      FileTypeRegistry fileTypeRegistry) {
    super(
        locale.fileStructureActionName(),
        locale.fileStructureActionDescription(),
        resources.fileNavigation(),
        editorAgent,
        fileTypeRegistry);

    this.fileStructurePresenter = fileStructurePresenter;
    this.editorAgent = editorAgent;
  }

  /** {@inheritDoc} */
  @Override
  public void actionPerformed(ActionEvent e) {
    fileStructurePresenter.show(editorAgent.getActiveEditor());
  }
}
