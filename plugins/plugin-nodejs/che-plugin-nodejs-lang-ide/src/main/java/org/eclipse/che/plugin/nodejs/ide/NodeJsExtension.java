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
package org.eclipse.che.plugin.nodejs.ide;

import com.google.inject.Inject;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;

/** @author Dmitry Shnurenko */
@Extension(title = "NodeJs")
public class NodeJsExtension {

  public static final String NODE_JS_CATEGORY = "Node.js";

  @Inject
  private void prepareActions(NodeJsResources resources, IconRegistry iconRegistry) {
    iconRegistry.registerIcon(
        new Icon(NODE_JS_CATEGORY + ".samples.category.icon", resources.jsIcon()));
  }
}
