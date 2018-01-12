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
package org.eclipse.che.plugin.maven.generator.archetype;

import static java.util.Collections.emptyMap;

import java.util.Map;
import org.eclipse.che.plugin.maven.shared.MavenArchetype;

/** @author Vitalii Parfonov */
public class MavenArchetypeImpl implements MavenArchetype {

  private String groupId;
  private String artifactId;
  private String version;
  private String repository;
  private Map<String, String> properties;

  public MavenArchetypeImpl(
      String groupId,
      String artifactId,
      String version,
      String repository,
      Map<String, String> properties) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.repository = repository;
    this.properties = properties;
  }

  @Override
  public String getGroupId() {
    return groupId;
  }

  @Override
  public String getArtifactId() {
    return artifactId;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public String getRepository() {
    return repository;
  }

  @Override
  public Map<String, String> getProperties() {
    if (properties == null) {
      return emptyMap();
    }
    return properties;
  }
}
