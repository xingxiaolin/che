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
package org.eclipse.che.plugin.languageserver.ide.editor.sync;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collections;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;

/**
 * Handles full text synchronization
 *
 * @author Evgen Vidolob
 */
@Singleton
class FullTextDocumentSynchronize implements TextDocumentSynchronize {

  private final DtoFactory dtoFactory;
  private final TextDocumentServiceClient textDocumentService;

  @Inject
  public FullTextDocumentSynchronize(
      DtoFactory dtoFactory, TextDocumentServiceClient textDocumentService) {
    this.dtoFactory = dtoFactory;
    this.textDocumentService = textDocumentService;
  }

  @Override
  public void syncTextDocument(
      Document document, TextPosition start, TextPosition end, String insertedText, int version) {

    DidChangeTextDocumentParams changeDTO = dtoFactory.createDto(DidChangeTextDocumentParams.class);
    String uri = document.getFile().getLocation().toString();
    changeDTO.setUri(uri);
    VersionedTextDocumentIdentifier versionedDocId =
        dtoFactory.createDto(VersionedTextDocumentIdentifier.class);
    versionedDocId.setUri(uri);
    versionedDocId.setVersion(version);
    changeDTO.setTextDocument(versionedDocId);
    TextDocumentContentChangeEvent actualChange =
        dtoFactory.createDto(TextDocumentContentChangeEvent.class);

    actualChange.setText(document.getContents());
    changeDTO.setContentChanges(Collections.singletonList(actualChange));
    textDocumentService.didChange(changeDTO);
  }
}
