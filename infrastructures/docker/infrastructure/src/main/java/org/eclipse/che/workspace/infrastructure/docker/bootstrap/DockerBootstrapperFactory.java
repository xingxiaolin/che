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
package org.eclipse.che.workspace.infrastructure.docker.bootstrap;

import com.google.inject.assistedinject.Assisted;
import java.util.List;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.installer.shared.model.Installer;
import org.eclipse.che.workspace.infrastructure.docker.DockerMachine;

/** @author Sergii Leshchenko */
public interface DockerBootstrapperFactory {
  DockerBootstrapper create(
      @Assisted String machineName,
      @Assisted RuntimeIdentity runtimeIdentity,
      @Assisted List<? extends Installer> installers,
      @Assisted DockerMachine dockerMachine);
}
