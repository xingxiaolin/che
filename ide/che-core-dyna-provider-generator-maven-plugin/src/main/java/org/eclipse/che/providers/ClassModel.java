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
package org.eclipse.che.providers;

/**
 * Data class for generator. Holds class name and variable name for class.
 *
 * @author Evgen Vidolob
 */
public class ClassModel {

  private String name;

  private String varName;

  public ClassModel(Class<?> clazz) {
    name = clazz.getName();
    varName = clazz.getName().replaceAll("\\.", "_");
  }

  public String getName() {
    return name;
  }

  public String getVarName() {
    return varName;
  }
}
