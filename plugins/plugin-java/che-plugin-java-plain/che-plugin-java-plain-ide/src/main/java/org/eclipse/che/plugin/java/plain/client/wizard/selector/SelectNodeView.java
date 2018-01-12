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
package org.eclipse.che.plugin.java.plain.client.wizard.selector;

import com.google.inject.ImplementedBy;
import java.util.List;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ui.smartTree.data.Node;

/**
 * Represents the structure of the current project. It needs for choosing source node.
 *
 * @author Valeriy Svydenko
 */
@ImplementedBy(SelectNodeViewImpl.class)
public interface SelectNodeView extends View<SelectNodeView.ActionDelegate> {
  /** Needs for delegate some function into SelectPath view. */
  interface ActionDelegate {
    /** Sets selected nodes. */
    void setSelectedNode(List<Node> selectedNodes);
  }

  /**
   * Show structure of the tree.
   *
   * @param nodes list of the project root nodes
   */
  void setStructure(List<Node> nodes);

  /** Show dialog. */
  void show();
}
