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
package org.eclipse.che.api.installer.server.impl;

import static java.lang.String.format;

import com.google.inject.Singleton;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.eclipse.che.api.installer.server.exception.InstallerException;
import org.eclipse.che.api.installer.shared.model.Installer;

/** @author Anatolii Bazko */
@Singleton
public class InstallerValidator {

  /**
   * Checks {@link Installer} for valid parameters.
   *
   * @param installer the installer to check
   * @throws InstallerException
   */
  public void validate(Installer installer) throws InstallerException {
    validateVersion(installer);
  }

  private void validateVersion(Installer installer) throws InstallerException {
    try {
      new ComparableVersion(installer.getVersion());
    } catch (Exception e) {
      throw new InstallerException(
          format(
              "Installer '%s' has illegal version format '%s'.",
              InstallerFqn.of(installer).toKey(), installer.getVersion()),
          e);
    }
  }
}
