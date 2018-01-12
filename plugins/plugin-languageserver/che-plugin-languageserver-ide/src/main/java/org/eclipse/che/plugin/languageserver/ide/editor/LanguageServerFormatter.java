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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.events.DocumentChangedEvent;
import org.eclipse.che.ide.api.editor.events.DocumentChangedHandler;
import org.eclipse.che.ide.api.editor.formatter.ContentFormatter;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.api.editor.text.TextRange;
import org.eclipse.che.ide.api.editor.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.editor.texteditor.UndoableEditor;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.editor.preferences.EditorPreferencesManager;
import org.eclipse.che.ide.editor.preferences.editorproperties.EditorProperties;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.FormattingOptions;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;

/** @author Evgen Vidolob */
public class LanguageServerFormatter implements ContentFormatter {

  private final TextDocumentServiceClient client;
  private final DtoFactory dtoFactory;
  private final NotificationManager manager;
  private final ServerCapabilities capabilities;
  private final EditorPreferencesManager editorPreferencesManager;
  private TextEditor editor;

  @Inject
  public LanguageServerFormatter(
      TextDocumentServiceClient client,
      DtoFactory dtoFactory,
      NotificationManager manager,
      @Assisted ServerCapabilities capabilities,
      EditorPreferencesManager editorPreferencesManager) {
    this.client = client;
    this.dtoFactory = dtoFactory;
    this.manager = manager;
    this.capabilities = capabilities;
    this.editorPreferencesManager = editorPreferencesManager;
  }

  @Override
  public void format(Document document) {

    TextRange selectedRange = document.getSelectedTextRange();
    if (selectedRange != null
        && !selectedRange.getFrom().equals(selectedRange.getTo())
        && capabilities.getDocumentRangeFormattingProvider()) {
      // selection formatting
      formatRange(selectedRange, document);
    } else if (capabilities.getDocumentFormattingProvider()) {
      // full document formatting
      formatFullDocument(document);
    }
  }

  @Override
  public void install(TextEditor editor) {
    this.editor = editor;
    if (capabilities.getDocumentOnTypeFormattingProvider() != null
        && capabilities.getDocumentOnTypeFormattingProvider().getFirstTriggerCharacter() != null) {
      editor
          .getDocument()
          .getDocumentHandle()
          .getDocEventBus()
          .addHandler(
              DocumentChangedEvent.TYPE,
              new DocumentChangedHandler() {
                @Override
                public void onDocumentChanged(DocumentChangedEvent event) {
                  if (capabilities
                      .getDocumentOnTypeFormattingProvider()
                      .getFirstTriggerCharacter()
                      .equals(event.getText())) {
                    Document document = event.getDocument().getDocument();

                    DocumentOnTypeFormattingParams params =
                        dtoFactory.createDto(DocumentOnTypeFormattingParams.class);
                    TextDocumentIdentifier identifier =
                        dtoFactory.createDto(TextDocumentIdentifier.class);
                    identifier.setUri(document.getFile().getLocation().toString());
                    params.setTextDocument(identifier);
                    params.setOptions(getFormattingOptions());
                    params.setCh(event.getText());

                    TextPosition position = document.getPositionFromIndex(event.getOffset());

                    Position start = dtoFactory.createDto(Position.class);
                    start.setLine(position.getLine());
                    start.setCharacter(position.getCharacter());
                    params.setPosition(start);

                    Promise<List<TextEdit>> promise = client.onTypeFormatting(params);
                    handleFormatting(promise, document);
                  }
                }
              });
    }
  }

  private void formatFullDocument(Document document) {
    DocumentFormattingParams params = dtoFactory.createDto(DocumentFormattingParams.class);

    TextDocumentIdentifier identifier = dtoFactory.createDto(TextDocumentIdentifier.class);
    identifier.setUri(document.getFile().getLocation().toString());

    params.setTextDocument(identifier);
    params.setOptions(getFormattingOptions());
    Promise<List<TextEdit>> promise = client.formatting(params);
    handleFormatting(promise, document);
  }

  private void handleFormatting(Promise<List<TextEdit>> promise, final Document document) {
    promise
        .then(
            new Operation<List<TextEdit>>() {
              @Override
              public void apply(List<TextEdit> arg) throws OperationException {
                applyEdits(arg, document);
              }
            })
        .catchError(
            new Operation<PromiseError>() {
              @Override
              public void apply(PromiseError arg) throws OperationException {
                manager.notify(arg.getMessage());
              }
            });
  }

  private void applyEdits(List<TextEdit> edits, Document document) {
    HandlesUndoRedo undoRedo = null;

    if (editor instanceof UndoableEditor) {
      undoRedo = ((UndoableEditor) editor).getUndoRedo();
    }
    try {
      if (undoRedo != null) {
        undoRedo.beginCompoundChange();
      }

      // #2437: apply the text edits from last to first to avoid messing up the document
      Collections.reverse(edits);
      for (TextEdit change : edits) {
        Range range = change.getRange();
        document.replace(
            range.getStart().getLine(),
            range.getStart().getCharacter(),
            range.getEnd().getLine(),
            range.getEnd().getCharacter(),
            change.getNewText());
      }
    } catch (final Exception e) {
      Log.error(getClass(), e);
    } finally {
      if (undoRedo != null) {
        undoRedo.endCompoundChange();
      }
    }
  }

  private FormattingOptions getFormattingOptions() {
    FormattingOptions options = dtoFactory.createDto(FormattingOptions.class);
    options.setInsertSpaces(Boolean.parseBoolean(getEditorProperty(EditorProperties.EXPAND_TAB)));
    options.setTabSize(Integer.parseInt(getEditorProperty(EditorProperties.TAB_SIZE)));
    return options;
  }

  private String getEditorProperty(EditorProperties property) {
    return editorPreferencesManager.getEditorPreferences().get(property.toString()).toString();
  }

  private void formatRange(TextRange selectedRange, Document document) {
    DocumentRangeFormattingParams params =
        dtoFactory.createDto(DocumentRangeFormattingParams.class);

    TextDocumentIdentifier identifier = dtoFactory.createDto(TextDocumentIdentifier.class);
    identifier.setUri(document.getFile().getLocation().toString());

    params.setTextDocument(identifier);
    params.setOptions(getFormattingOptions());

    Range range = dtoFactory.createDto(Range.class);
    Position start = dtoFactory.createDto(Position.class);
    Position end = dtoFactory.createDto(Position.class);

    start.setLine(selectedRange.getFrom().getLine());
    start.setCharacter(selectedRange.getFrom().getCharacter());

    end.setLine(selectedRange.getTo().getLine());
    end.setCharacter(selectedRange.getTo().getCharacter());

    range.setStart(start);
    range.setEnd(end);

    params.setRange(range);

    Promise<List<TextEdit>> promise = client.rangeFormatting(params);
    handleFormatting(promise, document);
  }
}
