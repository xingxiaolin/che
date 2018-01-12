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
package org.eclipse.che.plugin.languageserver.ide.rename;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.part.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.editorconfig.TextEditorConfiguration;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.plugin.languageserver.ide.LanguageServerLocalization;
import org.eclipse.che.plugin.languageserver.ide.editor.LanguageServerEditorConfiguration;
import org.eclipse.lsp4j.ServerCapabilities;

/** Action for rename feature */
@Singleton
public class LSRenameAction extends AbstractPerspectiveAction {

  private final EditorAgent editorAgent;
  private final RenamePresenter renamePresenter;
  private final WorkspaceAgent workspaceAgent;

  @Inject
  public LSRenameAction(
      LanguageServerLocalization localization,
      EditorAgent editorAgent,
      RenamePresenter renamePresenter,
      WorkspaceAgent workspaceAgent) {
    super(
        singletonList(PROJECT_PERSPECTIVE_ID),
        localization.renameActionTitle(),
        localization.renameActionTitle());
    this.editorAgent = editorAgent;
    this.renamePresenter = renamePresenter;
    this.workspaceAgent = workspaceAgent;
  }

  @Override
  public void updateInPerspective(ActionEvent event) {
    EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
    Presentation presentation = event.getPresentation();
    if (activeEditor != workspaceAgent.getActivePart()) {
      presentation.setEnabledAndVisible(false);
      return;
    }
    if (Objects.nonNull(activeEditor) && activeEditor instanceof TextEditor) {
      TextEditorConfiguration configuration = ((TextEditor) activeEditor).getConfiguration();
      if (configuration instanceof LanguageServerEditorConfiguration) {
        ServerCapabilities capabilities =
            ((LanguageServerEditorConfiguration) configuration).getServerCapabilities();
        presentation.setEnabledAndVisible(
            capabilities.getRenameProvider() != null && capabilities.getRenameProvider());
        return;
      }
    }
    presentation.setEnabledAndVisible(false);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
    if (Objects.nonNull(activeEditor) && activeEditor instanceof TextEditor) {
      renamePresenter.rename(((TextEditor) activeEditor));
    }
  }
}
