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
package org.eclipse.che.ide.console;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.name.Names;
import org.eclipse.che.ide.api.outputconsole.OutputConsole;

/** GIN module for configuring command consoles. */
public class ConsoleGinModule extends AbstractGinModule {
  @Override
  protected void configure() {
    bind(OutputConsoleView.class).to(OutputConsoleViewImpl.class);
    install(
        new GinFactoryModuleBuilder()
            .implement(
                CommandOutputConsole.class,
                Names.named("command"),
                CommandOutputConsolePresenter.class)
            .implement(OutputConsole.class, Names.named("default"), DefaultOutputConsole.class)
            .implement(OutputConsole.class, Names.named("composite"), CompositeOutputConsole.class)
            .build(CommandConsoleFactory.class));
  }
}
