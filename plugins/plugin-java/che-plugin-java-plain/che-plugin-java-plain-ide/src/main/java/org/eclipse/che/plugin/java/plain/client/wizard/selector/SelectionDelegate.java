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

import java.util.List;
import org.eclipse.che.ide.ui.smartTree.data.Node;

/**
 * Delegate which handles result of the node selection.
 *
 * @author Valeriy Svydenko
 */
public interface SelectionDelegate {

  /**
   * Fires when some nodes was selected.
   *
   * @param selectedNodes list of the selected nodes
   */
  void onNodeSelected(List<Node> selectedNodes);
}
