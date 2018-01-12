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
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.codeassist.HasCompletionInformation;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.documentation.QuickDocumentation;

/** @author Evgen Vidolob */
@Singleton
public class QuickDocumentationAction extends JavaEditorAction {

  private QuickDocumentation quickDocumentation;

  @Inject
  public QuickDocumentationAction(
      JavaLocalizationConstant constant,
      QuickDocumentation quickDocumentation,
      EditorAgent editorAgent,
      JavaResources resources,
      FileTypeRegistry fileTypeRegistry) {
    super(
        constant.actionQuickdocTitle(),
        constant.actionQuickdocDescription(),
        resources.quickDocumentation(),
        editorAgent,
        fileTypeRegistry);
    this.quickDocumentation = quickDocumentation;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
    if (activeEditor == null) {
      return;
    }
    if (activeEditor instanceof TextEditor && activeEditor instanceof HasCompletionInformation) {
      if (((TextEditor) activeEditor).getEditorWidget().isCompletionProposalsShowing()) {
        ((HasCompletionInformation) activeEditor).showCompletionInformation();
        return;
      }
    }
    quickDocumentation.showDocumentation();
  }
}
