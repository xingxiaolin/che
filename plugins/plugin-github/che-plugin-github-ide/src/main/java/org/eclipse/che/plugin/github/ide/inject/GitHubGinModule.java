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
package org.eclipse.che.plugin.github.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.oauth.OAuth2Authenticator;
import org.eclipse.che.ide.api.project.wizard.ImportWizardRegistrar;
import org.eclipse.che.plugin.github.ide.GitHubServiceClient;
import org.eclipse.che.plugin.github.ide.GitHubServiceClientImpl;
import org.eclipse.che.plugin.github.ide.authenticator.GitHubAuthenticatorImpl;
import org.eclipse.che.plugin.github.ide.importer.GitHubImportWizardRegistrar;

/** @author Andrey Plotnikov */
@ExtensionGinModule
public class GitHubGinModule extends AbstractGinModule {
  /** {@inheritDoc} */
  @Override
  protected void configure() {
    bind(GitHubServiceClient.class).to(GitHubServiceClientImpl.class).in(Singleton.class);
    GinMultibinder.newSetBinder(binder(), OAuth2Authenticator.class)
        .addBinding()
        .to(GitHubAuthenticatorImpl.class);
    GinMultibinder.newSetBinder(binder(), ImportWizardRegistrar.class)
        .addBinding()
        .to(GitHubImportWizardRegistrar.class);
  }
}
