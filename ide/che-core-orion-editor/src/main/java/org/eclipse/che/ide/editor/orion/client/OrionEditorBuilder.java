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
package org.eclipse.che.ide.editor.orion.client;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.che.ide.api.editor.defaulteditor.EditorBuilder;
import org.eclipse.che.ide.api.editor.editorconfig.DefaultTextEditorConfiguration;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;

/**
 * Builder for Orion editor.
 *
 * @author Artem Zatsarynnyi
 */
public class OrionEditorBuilder implements EditorBuilder {

  private final Provider<OrionEditorPresenter> orionTextEditorProvider;

  @Inject
  public OrionEditorBuilder(Provider<OrionEditorPresenter> orionTextEditorProvider) {
    this.orionTextEditorProvider = orionTextEditorProvider;
  }

  @Override
  public TextEditor buildEditor() {
    final OrionEditorPresenter editor = orionTextEditorProvider.get();
    editor.initialize(new DefaultTextEditorConfiguration());
    return editor;
  }
}
