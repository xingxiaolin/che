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
package org.eclipse.che.plugin.maven.shared;

import java.util.Map;

/** @author Vitalii Parfonov */
public interface MavenArchetype {

  /** Returns the archetype's groupId. */
  String getGroupId();

  /** Returns the archetype's artifactId. */
  String getArtifactId();

  /** Returns the archetype's version. */
  String getVersion();

  /** Returns the repository where to find the archetype. */
  String getRepository();

  /** Returns the additional properties for the archetype. */
  Map<String, String> getProperties();
}
