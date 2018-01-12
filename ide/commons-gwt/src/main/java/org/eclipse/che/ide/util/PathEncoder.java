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
package org.eclipse.che.ide.util;

import static com.google.gwt.http.client.URL.encodePathSegment;
import static org.eclipse.che.ide.resource.Path.SEPARATOR;

import org.eclipse.che.ide.resource.Path;

/**
 * @author Alexander Andrienko
 * @author Mykola Morhun
 */
public class PathEncoder {

  private PathEncoder() {}

  /** Returns path encoded by segments without device. */
  public static String encodePath(Path path) {
    StringBuilder encodedPath = new StringBuilder();

    if (path.hasLeadingSeparator()) {
      encodedPath.append(SEPARATOR);
    }

    String segment;
    for (int i = 0; i < path.segmentCount(); i++) {
      segment = path.segment(i);
      encodedPath.append(encodePathSegment(segment));
      encodedPath.append(SEPARATOR);
    }

    if (!path.isEmpty() && !path.isRoot() && !path.hasTrailingSeparator()) {
      encodedPath.deleteCharAt(encodedPath.length() - 1);
    }

    return encodedPath.toString();
  }
}
