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
package org.eclipse.che.ide.ext.git.client.compare.selectablechangespanel;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Set;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.ext.git.client.GitResources;
import org.eclipse.che.ide.ext.git.client.compare.changespanel.ChangesPanelViewImpl;
import org.eclipse.che.ide.project.shared.NodesResources;
import org.eclipse.che.ide.resource.Path;

/**
 * Implementation of {@link SelectableChangesPanelView}.
 *
 * @author Igor Vinokur
 */
@Singleton
public class SelectableChangesPanelViewImpl extends ChangesPanelViewImpl
    implements SelectableChangesPanelView {

  private CheckBoxRender render;

  @Inject
  public SelectableChangesPanelViewImpl(
      GitResources resources, GitLocalizationConstant locale, NodesResources nodesResources) {
    super(resources, locale, nodesResources);
  }

  @Override
  public void setDelegate(SelectableChangesPanelView.ActionDelegate delegate) {
    this.render = new CheckBoxRender(super.getTreeStyles(), delegate);
    super.setTreeRender(render);
  }

  @Override
  public void setMarkedCheckBoxes(Set<Path> paths) {
    render.setNodePaths(getNodePaths());
    paths.forEach(path -> render.handleCheckBoxSelection(path, false));
  }
}
