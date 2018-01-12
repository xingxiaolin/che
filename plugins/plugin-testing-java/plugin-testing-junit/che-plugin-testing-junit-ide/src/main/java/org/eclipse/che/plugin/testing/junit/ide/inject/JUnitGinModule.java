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
package org.eclipse.che.plugin.testing.junit.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.plugin.testing.ide.action.TestAction;
import org.eclipse.che.plugin.testing.ide.detector.TestFileExtension;
import org.eclipse.che.plugin.testing.junit.ide.JUnitTestFileExtension;
import org.eclipse.che.plugin.testing.junit.ide.action.JUnitTestActionGroup;

/** Gin module for JUnit extension. */
@ExtensionGinModule
public class JUnitGinModule extends AbstractGinModule {
  @Override
  protected void configure() {
    GinMultibinder.newSetBinder(binder(), TestAction.class)
        .addBinding()
        .to(JUnitTestActionGroup.class);
    GinMultibinder.newSetBinder(binder(), TestFileExtension.class)
        .addBinding()
        .to(JUnitTestFileExtension.class);
  }
}
