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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.ide.maven.tools.MavenArtifact;
import org.eclipse.che.plugin.maven.shared.MavenArchetype;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for {@link ArchetypeGenerator}
 *
 * @author Vitalii Parfonov
 */
public class ArchetypeGeneratorTest {

  @Test
  @Ignore
  public void generateFromArchetype() throws Exception {
    MavenArchetype mavenArchetype = mock(MavenArchetype.class);
    EventService eventService = mock(EventService.class);
    when(mavenArchetype.getArtifactId()).thenReturn("tomee-webapp-archetype");
    when(mavenArchetype.getGroupId()).thenReturn("org.apache.openejb.maven");
    when(mavenArchetype.getVersion()).thenReturn("1.7.1");
    File workDir = Files.createTempDirectory("workDir").toFile();
    ArchetypeGenerator archetypeGenerator = new ArchetypeGenerator(eventService);
    String artifactId = NameGenerator.generate("artifactId", 5);
    String groupId = NameGenerator.generate("groupId", 5);
    MavenArtifact mavenArtifact = new MavenArtifact();
    mavenArtifact.setArtifactId(artifactId);
    mavenArtifact.setGroupId(groupId);
    mavenArtifact.setVersion("1.0-SNAPSHOT");
    archetypeGenerator.generateFromArchetype(workDir, mavenArchetype, mavenArtifact);
    String[] list = workDir.list();
    List<String> strings = Arrays.asList(list);
    Assert.assertTrue(strings.contains(artifactId));
  }
}
