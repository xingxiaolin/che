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
package org.eclipse.che.ide.part.explorer.project;

import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.data.NodeInterceptor;

/**
 * Default implementation of node interceptor that do nothing. Just need to proper initialization of
 * ide components at startup.
 *
 * @author Vitalii Parfonov
 * @author Vlad Zhukovskyi
 */
@Singleton
public class DefaultNodeInterceptor implements NodeInterceptor {
  /** {@inheritDoc} */
  @Override
  public Promise<List<Node>> intercept(Node parent, List<Node> children) {
    return Promises.resolve(children);
  }

  /** {@inheritDoc} */
  @Override
  public int getPriority() {
    return MAX_PRIORITY;
  }
}
