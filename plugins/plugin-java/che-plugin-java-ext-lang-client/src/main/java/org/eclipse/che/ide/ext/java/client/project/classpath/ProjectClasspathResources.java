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
package org.eclipse.che.ide.ext.java.client.project.classpath;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Class contains references to resources which need to correct displaying of command wizard dialog.
 *
 * @author Valeriy Svydenko
 */
public interface ProjectClasspathResources extends ClientBundle {

  interface ClasspathStyles extends CssResource {

    String categoryHeader();

    String elementHeader();

    String elementLabel();

    String disableButton();

    String selectNode();

    String classpathCategoryLabel();

    String removeButton();

    @ClassName("classpath-entry-category")
    String classpathEntryCategory();
  }

  @Source({"PropertiesRenderer.css", "org/eclipse/che/ide/api/ui/style.css"})
  ClasspathStyles getCss();

  @Source("remove-node-button.svg")
  SVGResource removeNode();
}
