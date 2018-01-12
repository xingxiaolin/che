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
package org.eclipse.che.ide.actions;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.part.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

import com.google.inject.Inject;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.texteditor.HandlesTextOperations;
import org.eclipse.che.ide.api.editor.texteditor.TextEditorOperations;

/**
 * Formatter Action
 *
 * @author Roman Nikitenko
 * @author Dmitry Shnurenko
 */
public class FormatterAction extends AbstractPerspectiveAction {

  private final EditorAgent editorAgent;

  @Inject
  public FormatterAction(
      EditorAgent editorAgent, CoreLocalizationConstant localization, Resources resources) {
    super(
        singletonList(PROJECT_PERSPECTIVE_ID),
        localization.formatName(),
        localization.formatDescription(),
        resources.format());
    this.editorAgent = editorAgent;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final EditorPartPresenter editor = editorAgent.getActiveEditor();
    HandlesTextOperations handlesOperations;
    if (editor instanceof HandlesTextOperations) {
      handlesOperations = (HandlesTextOperations) editor;
      if (handlesOperations.canDoOperation(TextEditorOperations.FORMAT)) {
        handlesOperations.doOperation(TextEditorOperations.FORMAT);
      }
    }
  }

  @Override
  public void updateInPerspective(@NotNull ActionEvent event) {
    final EditorPartPresenter editor = editorAgent.getActiveEditor();
    boolean isCanDoOperation = false;

    HandlesTextOperations handlesOperations;
    if (editor instanceof HandlesTextOperations) {
      handlesOperations = (HandlesTextOperations) editor;
      isCanDoOperation = handlesOperations.canDoOperation(TextEditorOperations.FORMAT);
    }

    event.getPresentation().setEnabled(isCanDoOperation);
  }
}
