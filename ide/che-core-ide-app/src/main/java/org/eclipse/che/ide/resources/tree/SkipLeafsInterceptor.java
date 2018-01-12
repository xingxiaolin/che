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
package org.eclipse.che.ide.resources.tree;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.data.NodeInterceptor;

/** @author Vlad Zhukovskiy */
public class SkipLeafsInterceptor implements NodeInterceptor {

  private final PromiseProvider promises;

  @Inject
  public SkipLeafsInterceptor(PromiseProvider promises) {
    this.promises = promises;
  }

  @Override
  public Promise<List<Node>> intercept(Node parent, List<Node> children) {
    List<Node> nodes = new ArrayList<>();

    for (Node node : children) {
      if (!node.isLeaf()) {
        nodes.add(node);
      }
    }

    return promises.resolve(nodes);
  }

  @Override
  public int getPriority() {
    return MIN_PRIORITY;
  }
}
