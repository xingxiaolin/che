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
package org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.selectnode.interceptors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.ext.java.client.tree.PackageNode;
import org.eclipse.che.ide.ext.java.shared.ClasspathEntryKind;
import org.eclipse.che.ide.ui.smartTree.data.Node;

/**
 * Class for intercepting source folders.
 *
 * @author Valeriy Svydenko
 */
public class SourceFolderNodeInterceptor implements ClasspathNodeInterceptor {
  @Override
  public boolean isNodeValid(Node node) {
    return !node.isLeaf();
  }

  @Override
  public int getKind() {
    return ClasspathEntryKind.SOURCE;
  }

  @Override
  public Promise<List<Node>> intercept(Node parent, List<Node> children) {
    List<Node> nodes = new ArrayList<>();

    for (Node child : children) {
      if (child.isLeaf() || child instanceof PackageNode) {
        continue;
      }

      nodes.add(child);
    }

    return Promises.resolve(nodes);
  }

  @Override
  public int getPriority() {
    return NORM_PRIORITY;
  }
}
