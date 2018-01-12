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
package org.eclipse.che.ide.ext.java.shared.dto.model;

import java.util.List;
import org.eclipse.che.dto.shared.DTO;

/**
 * DTO represents Java project in terms of JavaModel
 *
 * @author Evgen Vidolob
 */
@DTO
public interface JavaProject {

  /**
   * Project workspace path
   *
   * @return the path
   */
  String getPath();

  void setPath(String path);

  /**
   * Project name;
   *
   * @return name of the project
   */
  String getName();

  void setName(String name);

  /**
   * Get all package fragment roots from this project
   *
   * @return list of the package fragment roots
   */
  List<PackageFragmentRoot> getPackageFragmentRoots();

  void setPackageFragmentRoots(List<PackageFragmentRoot> roots);
}
