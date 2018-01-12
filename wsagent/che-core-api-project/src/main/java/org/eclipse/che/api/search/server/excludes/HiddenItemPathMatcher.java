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
package org.eclipse.che.api.search.server.excludes;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import javax.inject.Singleton;

/**
 * Performs match operation on paths to test whether specified item is hidden.
 *
 * @author Roman Nikitenko
 */
@Singleton
public class HiddenItemPathMatcher implements PathMatcher {

  @Override
  public boolean matches(Path path) {
    for (Path pathElement : path) {
      if (pathElement != null && pathElement.toFile().isHidden()) {
        return true;
      }
    }
    return false;
  }
}
