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
package org.eclipse.che.plugin.languageserver.ide.editor.codeassist;

import static org.eclipse.che.ide.api.theme.Style.theme;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.languageserver.shared.model.ExtendedCompletionItem;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.editor.codeassist.Completion;
import org.eclipse.che.ide.api.editor.codeassist.CompletionProposal;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.link.HasLinkedMode;
import org.eclipse.che.ide.api.editor.link.LinkedModel;
import org.eclipse.che.ide.api.editor.text.LinearRange;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.filters.Match;
import org.eclipse.che.ide.util.Pair;
import org.eclipse.che.plugin.languageserver.ide.LanguageServerResources;
import org.eclipse.che.plugin.languageserver.ide.editor.codeassist.snippet.SnippetResolver;
import org.eclipse.che.plugin.languageserver.ide.editor.quickassist.ApplyWorkspaceEditAction;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextEdit;

/**
 * @author Anatolii Bazko
 * @author Kaloyan Raev
 */
public class CompletionItemBasedCompletionProposal implements CompletionProposal {

  private final String currentWord;
  private final TextDocumentServiceClient documentServiceClient;
  private final LanguageServerResources resources;
  private final Icon icon;
  private final ServerCapabilities serverCapabilities;
  private final List<Match> highlights;
  private final int offset;
  private ExtendedCompletionItem completionItem;
  private boolean resolved;
  private HasLinkedMode editor;

  CompletionItemBasedCompletionProposal(
      HasLinkedMode editor,
      ExtendedCompletionItem completionItem,
      String currentWord,
      TextDocumentServiceClient documentServiceClient,
      LanguageServerResources resources,
      Icon icon,
      ServerCapabilities serverCapabilities,
      List<Match> highlights,
      int offset) {
    this.editor = editor;
    this.completionItem = completionItem;
    this.currentWord = currentWord;
    this.documentServiceClient = documentServiceClient;
    this.resources = resources;
    this.icon = icon;
    this.serverCapabilities = serverCapabilities;
    this.highlights = highlights;
    this.offset = offset;
    this.resolved = false;
  }

  @Override
  public void getAdditionalProposalInfo(final AsyncCallback<Widget> callback) {
    if (completionItem.getItem().getDocumentation() == null && canResolve()) {
      resolve()
          .then(
              item -> {
                completionItem = item;
                resolved = true;
                callback.onSuccess(createAdditionalInfoWidget());
              })
          .catchError(
              e -> {
                callback.onFailure(e.getCause());
              });
    } else {
      callback.onSuccess(createAdditionalInfoWidget());
    }
  }

  private Widget createAdditionalInfoWidget() {
    String documentation = completionItem.getItem().getDocumentation();
    if (documentation == null || documentation.trim().isEmpty()) {
      documentation = "No documentation found.";
    }

    HTML widget = new HTML(documentation);
    widget.setWordWrap(true);
    widget.getElement().getStyle().setColor(theme.completionPopupItemTextColor());
    widget.getElement().getStyle().setFontSize(13, Style.Unit.PX);
    widget.getElement().getStyle().setMarginLeft(4, Style.Unit.PX);
    widget.getElement().getStyle().setOverflow(Overflow.AUTO);
    widget.getElement().getStyle().setProperty("userSelect", "text");
    widget.setHeight("100%");
    return widget;
  }

  @Override
  public String getDisplayString() {
    SafeHtmlBuilder builder = new SafeHtmlBuilder();

    String label = completionItem.getItem().getLabel();
    int pos = 0;
    for (Match highlight : highlights) {
      if (highlight.getStart() == highlight.getEnd()) {
        continue;
      }

      if (pos < highlight.getStart()) {
        appendPlain(builder, label.substring(pos, highlight.getStart()));
      }

      appendHighlighted(builder, label.substring(highlight.getStart(), highlight.getEnd()));
      pos = highlight.getEnd();
    }

    if (pos < label.length()) {
      appendPlain(builder, label.substring(pos));
    }

    if (completionItem.getItem().getDetail() != null) {
      appendDetail(builder, completionItem.getItem().getDetail());
    }

    return builder.toSafeHtml().asString();
  }

  private void appendPlain(SafeHtmlBuilder builder, String text) {
    builder.appendEscaped(text);
  }

  private void appendHighlighted(SafeHtmlBuilder builder, String text) {
    builder.appendHtmlConstant("<span class=\"" + resources.css().codeassistantHighlight() + "\">");
    builder.appendEscaped(text);
    builder.appendHtmlConstant("</span>");
  }

  private void appendDetail(SafeHtmlBuilder builder, String text) {
    builder.appendHtmlConstant(" <span class=\"" + resources.css().codeassistantDetail() + "\">");
    builder.appendEscaped(text);
    builder.appendHtmlConstant("</span>");
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public void getCompletion(final CompletionCallback callback) {
    if (canResolve()) {
      resolve()
          .then(
              completionItem -> {
                callback.onCompletion(
                    new CompletionImpl(editor, completionItem.getItem(), currentWord, offset));
              });
    } else {
      callback.onCompletion(
          new CompletionImpl(editor, completionItem.getItem(), currentWord, offset));
    }
  }

  private boolean canResolve() {
    return !resolved
        && serverCapabilities.getCompletionProvider() != null
        && serverCapabilities.getCompletionProvider().getResolveProvider() != null
        && serverCapabilities.getCompletionProvider().getResolveProvider();
  }

  private Promise<ExtendedCompletionItem> resolve() {
    return documentServiceClient.resolveCompletionItem(completionItem);
  }

  @VisibleForTesting
  static class CompletionImpl implements Completion {
    private CompletionItem completionItem;
    private String currentWord;
    private int offset;
    private LinearRange lastSelection;
    private HasLinkedMode editor;

    public CompletionImpl(
        HasLinkedMode editor, CompletionItem completionItem, String currentWord, int offset) {
      this.editor = editor;
      this.completionItem = completionItem;
      this.currentWord = currentWord;
      this.offset = offset;
    }

    @Override
    public void apply(Document document) {
      List<TextEdit> edits = new ArrayList<>();
      TextPosition cursorPosition = document.getCursorPosition();
      if (completionItem.getTextEdit() != null) {
        edits.add(adjustForOffset(completionItem.getTextEdit(), cursorPosition, offset));
      } else if (completionItem.getInsertText() == null) {
        edits.add(
            new TextEdit(
                newRange(
                    cursorPosition.getLine(),
                    cursorPosition.getCharacter() - currentWord.length(),
                    cursorPosition.getLine(),
                    cursorPosition.getCharacter()),
                completionItem.getLabel()));
      } else {
        edits.add(
            new TextEdit(
                newRange(
                    cursorPosition.getLine(),
                    cursorPosition.getCharacter() - currentWord.length(),
                    cursorPosition.getLine(),
                    cursorPosition.getCharacter()),
                completionItem.getInsertText()));
      }
      if (completionItem.getAdditionalTextEdits() != null) {
        completionItem
            .getAdditionalTextEdits()
            .forEach(e -> edits.add(adjustForOffset(e, cursorPosition, offset)));
      }
      TextEdit firstEdit = edits.get(0);
      if (completionItem.getInsertTextFormat() == InsertTextFormat.Snippet) {
        Position startPos = firstEdit.getRange().getStart();
        TextPosition startTextPosition =
            new TextPosition(startPos.getLine(), startPos.getCharacter());
        int startOffset = document.getIndexFromPosition(startTextPosition);
        Pair<String, LinkedModel> resolved =
            new SnippetResolver(new DocumentVariableResolver(document, startTextPosition))
                .resolve(firstEdit.getNewText(), editor, startOffset);
        firstEdit.setNewText(resolved.first);
        ApplyWorkspaceEditAction.applyTextEdits(document, edits);
        if (resolved.second != null) {
          editor.getLinkedMode().enterLinkedMode(resolved.second);
          lastSelection = null;
        } else {
          lastSelection = computeLastSelection(document, firstEdit);
        }
      } else {
        ApplyWorkspaceEditAction.applyTextEdits(document, edits);
        lastSelection = computeLastSelection(document, firstEdit);
      }
    }

    private LinearRange computeLastSelection(Document document, TextEdit textEdit) {
      Range range = textEdit.getRange();
      TextPosition textPosition =
          new TextPosition(range.getStart().getLine(), range.getStart().getCharacter());
      int startOffset =
          document.getIndexFromPosition(textPosition) + textEdit.getNewText().length();
      return LinearRange.createWithStart(startOffset).andLength(0);
    }

    private Range newRange(int startLine, int startChar, int endLine, int endChar) {
      return new Range(new Position(startLine, startChar), new Position(endLine, endChar));
    }

    private TextEdit adjustForOffset(TextEdit textEdit, TextPosition pos, int delta) {
      Range range = textEdit.getRange();
      int originalStart = pos.getCharacter() - delta;
      if (range.getStart().getLine() != pos.getLine()
          || textEdit.getRange().getEnd().getCharacter() < originalStart) {
        return textEdit;
      } else if (originalStart < range.getStart().getCharacter()) {
        return new TextEdit(
            newRange(
                range.getStart().getLine(),
                range.getStart().getCharacter() + delta,
                range.getEnd().getLine(),
                range.getEnd().getCharacter() + delta),
            textEdit.getNewText());
      } else {
        return new TextEdit(
            newRange(
                range.getStart().getLine(),
                range.getStart().getCharacter(),
                range.getEnd().getLine(),
                range.getEnd().getCharacter() + delta),
            textEdit.getNewText());
      }
    }

    @Override
    public LinearRange getSelection(Document document) {
      return lastSelection;
    }
  }
}
