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
package org.eclipse.che.plugin.languageserver.ide.editor;

import javax.inject.Inject;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.editor.AsyncEditorProvider;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorProvider;
import org.eclipse.che.ide.api.editor.defaulteditor.AbstractTextEditorProvider;
import org.eclipse.che.ide.api.editor.defaulteditor.EditorBuilder;
import org.eclipse.che.ide.api.editor.editorconfig.DefaultTextEditorConfiguration;
import org.eclipse.che.ide.api.editor.editorconfig.TextEditorConfiguration;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.plugin.languageserver.ide.registry.LanguageServerRegistry;
import org.eclipse.lsp4j.ServerCapabilities;

/** Provide editor with LS support */
public class LanguageServerEditorProvider implements AsyncEditorProvider, EditorProvider {

  private final LanguageServerRegistry registry;
  private final LanguageServerEditorConfigurationFactory editorConfigurationFactory;

  @com.google.inject.Inject private EditorBuilder editorBuilder;

  @Inject
  public LanguageServerEditorProvider(
      LanguageServerEditorConfigurationFactory editorConfigurationFactory,
      LanguageServerRegistry registry,
      LoaderFactory loaderFactory) {
    this.editorConfigurationFactory = editorConfigurationFactory;
    this.registry = registry;
  }

  @Override
  public String getId() {
    return "LanguageServerEditor";
  }

  @Override
  public String getDescription() {
    return "Code Editor";
  }

  @Override
  public TextEditor getEditor() {
    if (editorBuilder == null) {
      Log.debug(
          AbstractTextEditorProvider.class,
          "No builder registered for default editor type - giving up.");
      return null;
    }

    final TextEditor editor = editorBuilder.buildEditor();
    editor.initialize(new DefaultTextEditorConfiguration());
    return editor;
  }

  @Override
  public Promise<EditorPartPresenter> createEditor(VirtualFile file) {
    if (file instanceof File) {
      File resource = (File) file;

      Promise<ServerCapabilities> promise =
          registry.getOrInitializeServer(resource.getProject().getPath(), file);
      return promise.then(
          new Function<ServerCapabilities, EditorPartPresenter>() {
            @Override
            public EditorPartPresenter apply(ServerCapabilities capabilities)
                throws FunctionException {
              if (editorBuilder == null) {
                Log.debug(
                    AbstractTextEditorProvider.class,
                    "No builder registered for default editor type - giving up.");
                return null;
              }

              final TextEditor editor = editorBuilder.buildEditor();
              TextEditorConfiguration configuration =
                  capabilities == null
                      ? new DefaultTextEditorConfiguration()
                      : editorConfigurationFactory.build(editor, capabilities);
              editor.initialize(configuration);
              return editor;
            }
          });
    }
    return null;
  }
}
