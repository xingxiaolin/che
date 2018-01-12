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
package org.eclipse.che.plugin.maven.client.wizard;

import com.google.inject.ImplementedBy;
import java.util.List;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.plugin.maven.client.MavenArchetype;

/** @author Evgen Vidolob */
@ImplementedBy(MavenPageViewImpl.class)
public interface MavenPageView extends View<MavenPageView.ActionDelegate> {
  String getPackaging();

  void setPackaging(String packaging);

  MavenArchetype getArchetype();

  void setArchetypes(List<MavenArchetype> archetypes);

  String getGroupId();

  void setGroupId(String group);

  String getArtifactId();

  void setArtifactId(String artifact);

  String getVersion();

  void setVersion(String value);

  void setPackagingVisibility(boolean visible);

  void setArchetypeSectionVisibility(boolean visible);

  void enableArchetypes(boolean enabled);

  boolean isGenerateFromArchetypeSelected();

  void showArtifactIdMissingIndicator(boolean doShow);

  void showGroupIdMissingIndicator(boolean doShow);

  void showVersionMissingIndicator(boolean doShow);

  void clearArchetypes();

  public interface ActionDelegate {
    void onCoordinatesChanged();

    void packagingChanged(String packaging);

    void generateFromArchetypeChanged(boolean isGenerateFromArchetype);

    void archetypeChanged(MavenArchetype archetype);
  }
}
