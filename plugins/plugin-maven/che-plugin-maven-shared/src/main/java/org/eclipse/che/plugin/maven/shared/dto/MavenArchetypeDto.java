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
package org.eclipse.che.plugin.maven.shared.dto;

import java.util.Map;
import org.eclipse.che.dto.shared.DTO;
import org.eclipse.che.plugin.maven.shared.MavenArchetype;

/**
 * DTO that describes Maven archetype to use for project generation.
 *
 * @author Artem Zatsarynnyi
 */
@DTO
public interface MavenArchetypeDto extends MavenArchetype {

  void setGroupId(String groupId);

  MavenArchetypeDto withGroupId(String groupId);

  void setArtifactId(String artifactId);

  MavenArchetypeDto withArtifactId(String artifactId);

  void setVersion(String version);

  MavenArchetypeDto withVersion(String version);

  void setRepository(String repository);

  MavenArchetypeDto withRepository(String repository);

  void setProperties(Map<String, String> properties);

  MavenArchetypeDto withProperties(Map<String, String> properties);
}
