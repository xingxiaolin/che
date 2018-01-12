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
package org.eclipse.che.api.languageserver.registry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.CompletableFuture;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.languageserver.launcher.LanguageServerLauncher;
import org.eclipse.che.api.languageserver.shared.model.LanguageDescription;
import org.eclipse.che.commons.lang.Pair;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageServer;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** @author Anatoliy Bazko */
@Listeners(MockitoTestNGListener.class)
public class ServerInitializerImplTest {

  @Mock private ServerInitializerObserver observer;
  @Mock private LanguageDescription languageDescription;
  @Mock private LanguageServerDescription serverDescription;
  @Mock private LanguageServerLauncher launcher;
  @Mock private LanguageServer server;
  @Mock private EventService eventService;

  private CompletableFuture<InitializeResult> completableFuture;
  private ServerInitializerImpl initializer;

  @BeforeMethod
  public void setUp() throws Exception {
    initializer = spy(new ServerInitializerImpl());
    completableFuture =
        CompletableFuture.completedFuture(new InitializeResult(new ServerCapabilities()));
  }

  @Test
  public void initializerShouldNotifyObservers() throws Exception {
    when(languageDescription.getLanguageId()).thenReturn("languageId");
    when(server.initialize(any(InitializeParams.class))).thenReturn(completableFuture);

    when(launcher.launch(anyString(), any())).thenReturn(server);
    when(launcher.getDescription()).thenReturn(serverDescription);
    when(serverDescription.getId()).thenReturn("launcherId");
    doNothing().when(initializer).registerCallbacks(any(), any());

    initializer.addObserver(observer);
    Pair<LanguageServer, InitializeResult> initResult =
        initializer.initialize(launcher, null, "/path").get();

    assertEquals(server, initResult.first);
    verify(observer, timeout(2000))
        .onServerInitialized(eq(launcher), eq(server), any(ServerCapabilities.class), eq("/path"));
  }
}
