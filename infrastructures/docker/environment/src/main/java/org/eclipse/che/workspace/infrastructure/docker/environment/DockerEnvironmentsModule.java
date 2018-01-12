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
package org.eclipse.che.workspace.infrastructure.docker.environment;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironmentFactory;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.ComposeEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.ComposeEnvironmentFactory;
import org.eclipse.che.workspace.infrastructure.docker.environment.dockerfile.DockerfileEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.environment.dockerfile.DockerfileEnvironmentFactory;
import org.eclipse.che.workspace.infrastructure.docker.environment.dockerimage.DockerImageEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.environment.dockerimage.DockerImageEnvironmentFactory;

/** @author Sergii Leshchenko */
public class DockerEnvironmentsModule extends AbstractModule {
  @Override
  protected void configure() {
    MapBinder<String, InternalEnvironmentFactory> factories =
        MapBinder.newMapBinder(binder(), String.class, InternalEnvironmentFactory.class);

    factories.addBinding(ComposeEnvironment.TYPE).to(ComposeEnvironmentFactory.class);
    factories.addBinding(DockerImageEnvironment.TYPE).to(DockerImageEnvironmentFactory.class);
    factories.addBinding(DockerfileEnvironment.TYPE).to(DockerfileEnvironmentFactory.class);
  }
}
