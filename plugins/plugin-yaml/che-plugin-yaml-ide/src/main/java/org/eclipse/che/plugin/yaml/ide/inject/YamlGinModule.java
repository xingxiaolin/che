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
package org.eclipse.che.plugin.yaml.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.plugin.yaml.ide.YamlServiceClient;
import org.eclipse.che.plugin.yaml.ide.YamlServiceClientImpl;
import org.eclipse.che.plugin.yaml.ide.preferences.YamlExtensionManagerPresenter;
import org.eclipse.che.plugin.yaml.ide.preferences.YamlExtensionManagerView;
import org.eclipse.che.plugin.yaml.ide.preferences.YamlExtensionManagerViewImpl;

/**
 * Gin module for Yaml support.
 *
 * @author Joshua Pinkney
 */
@ExtensionGinModule
public class YamlGinModule extends AbstractGinModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    bind(YamlServiceClient.class).to(YamlServiceClientImpl.class).in(Singleton.class);

    bind(YamlExtensionManagerView.class).to(YamlExtensionManagerViewImpl.class).in(Singleton.class);
    GinMultibinder<PreferencePagePresenter> prefBinder =
        GinMultibinder.newSetBinder(binder(), PreferencePagePresenter.class);
    prefBinder.addBinding().to(YamlExtensionManagerPresenter.class);
  }
}
