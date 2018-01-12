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
package org.eclipse.che.ide.ext.git.client.compare;

import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.ADDED;
import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.COPIED;
import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.DELETED;
import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.MODIFIED;
import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.RENAMED;
import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.UNMODIFIED;
import static org.eclipse.che.ide.ext.git.client.compare.FileStatus.Status.UNTRACKED;

/**
 * Class for determining git status of given changed file.
 *
 * @author Igor Vinokur
 */
public class FileStatus {

  /** Git statuses. */
  public enum Status {
    MODIFIED,
    ADDED,
    DELETED,
    COPIED,
    RENAMED,
    UNTRACKED,
    UNMODIFIED
  }

  /**
   * determining git status of changed file.
   *
   * @param status String representation of git status
   */
  public static Status defineStatus(String status) {
    switch (status) {
      case "M":
        return MODIFIED;
      case "D":
        return DELETED;
      case "A":
        return ADDED;
      case "R":
        return RENAMED;
      case "C":
        return COPIED;
      case "U":
        return UNTRACKED;
    }
    return UNMODIFIED;
  }
}
