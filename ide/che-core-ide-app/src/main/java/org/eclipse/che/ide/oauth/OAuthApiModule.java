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
package org.eclipse.che.ide.oauth;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.oauth.OAuth2Authenticator;
import org.eclipse.che.ide.api.oauth.OAuth2AuthenticatorRegistry;

/**
 * GIN module for configuring OAuth API components.
 *
 * @author Artem Zatsarynnyi
 */
public class OAuthApiModule extends AbstractGinModule {

  @Override
  protected void configure() {

    GinMultibinder.newSetBinder(binder(), OAuth2Authenticator.class)
        .addBinding()
        .to(DefaultOAuthAuthenticatorImpl.class);

    bind(OAuth2AuthenticatorRegistry.class)
        .to(OAuth2AuthenticatorRegistryImpl.class)
        .in(Singleton.class);
  }
}
