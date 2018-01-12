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
package org.eclipse.che.plugin.maven.client.resource;

import com.google.common.annotations.Beta;
import com.google.inject.Singleton;
import org.eclipse.che.ide.ext.java.client.resource.SourceFolderInterceptor;
import org.eclipse.che.ide.ext.java.shared.ContentRoot;
import org.eclipse.che.plugin.maven.shared.MavenAttributes;

/** @author Vlad Zhukovskiy */
@Beta
@Singleton
public class MavenSourceFolderInterceptor extends SourceFolderInterceptor {

  public MavenSourceFolderInterceptor() {}

  @Override
  protected ContentRoot getContentRoot() {
    return ContentRoot.TEST_SOURCE;
  }

  @Override
  protected String getAttribute() {
    return MavenAttributes.TEST_SOURCE_FOLDER;
  }
}
