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
package org.eclipse.che.api.watcher.server.impl;

import static org.eclipse.che.api.watcher.server.impl.FileWatcherUtils.toNormalPath;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Consumer;
import org.eclipse.che.api.watcher.server.FileWatcherManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link FileWatcherManager} */
@RunWith(MockitoJUnitRunner.class)
public class FileWatcherManagerTest {
  private static final String PATH = "/path";
  private static final int ID = 0;

  @Rule public TemporaryFolder rootFolder = new TemporaryFolder();

  @Mock FileWatcherByPathValue fileWatcherByPathValue;
  @Mock FileWatcherByPathMatcher fileWatcherByPathMatcher;
  @Mock FileWatcherExcludePatternsRegistry fileWatcherExcludePatternsRegistry;
  @Mock FileWatcherService service;

  FileWatcherManager manager;

  @Mock Consumer<String> create;
  @Mock Consumer<String> modify;
  @Mock Consumer<String> delete;
  @Mock PathMatcher pathMatcher;

  @Before
  public void setUp() throws Exception {
    manager =
        new SimpleFileWatcherManager(
            rootFolder.getRoot(),
            fileWatcherByPathValue,
            fileWatcherByPathMatcher,
            fileWatcherExcludePatternsRegistry);
  }

  @Test
  public void shouldWatchByPath() throws Exception {
    manager.registerByPath(PATH, create, modify, delete);

    Path path = toNormalPath(rootFolder.getRoot().toPath(), PATH);

    verify(fileWatcherByPathValue).watch(path, create, modify, delete);
  }

  @Test
  public void shouldUnWatchByPath() throws Exception {
    manager.unRegisterByPath(ID);

    verify(fileWatcherByPathValue).unwatch(ID);
  }

  @Test
  public void shouldWatchByMatcher() throws Exception {
    manager.registerByMatcher(pathMatcher, create, modify, delete);

    verify(fileWatcherByPathMatcher).watch(pathMatcher, create, modify, delete);
  }

  @Test
  public void shouldUnWatchByMatcher() throws Exception {
    manager.unRegisterByMatcher(ID);

    verify(fileWatcherByPathMatcher).unwatch(ID);
  }
}
