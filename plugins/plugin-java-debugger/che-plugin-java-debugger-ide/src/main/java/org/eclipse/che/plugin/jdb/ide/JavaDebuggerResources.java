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
package org.eclipse.che.plugin.jdb.ide;

import com.google.gwt.resources.client.ClientBundle;
import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Artem Zatsarynnyi */
public interface JavaDebuggerResources extends ClientBundle {

  /** Returns the icon for Java debug configuration type. */
  @Source("configuration/java-debug-configuration-type.svg")
  SVGResource javaDebugConfigurationType();
}
