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
package org.eclipse.che.api.languageserver.shared.model;

import java.util.List;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;

/** */
public class ExtendedTextDocumentEdit {
  private VersionedTextDocumentIdentifier textDocument;
  private List<ExtendedTextEdit> edits;

  public ExtendedTextDocumentEdit() {}

  public ExtendedTextDocumentEdit(
      VersionedTextDocumentIdentifier textDocument, List<ExtendedTextEdit> edits) {
    this.textDocument = textDocument;
    this.edits = edits;
  }

  public VersionedTextDocumentIdentifier getTextDocument() {
    return textDocument;
  }

  public void setTextDocument(VersionedTextDocumentIdentifier textDocument) {
    this.textDocument = textDocument;
  }

  public List<ExtendedTextEdit> getEdits() {
    return edits;
  }

  public void setEdits(List<ExtendedTextEdit> edits) {
    this.edits = edits;
  }
}
