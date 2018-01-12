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
package org.eclipse.che.infrastructure.docker.client.params;

/**
 * Contains util methods for {@code *Params} classes.
 *
 * @author Mykola Morhun
 * @author Alexander Garagatyi
 */
public class ParamsUtils {

  /**
   * Checks is given array non empty.<br>
   * Throws {@link IllegalArgumentException} if array doesn't contain elements.
   *
   * @param array array for check
   * @throws IllegalArgumentException if given array is empty
   */
  public static void requireNonEmptyArray(Object[] array) {
    if (array.length == 0) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Checks whether provided string is NULL or empty.
   *
   * @throws NullPointerException if provided argument is null
   * @throws IllegalArgumentException if provided argument is empty
   */
  public static void requireNonNullNorEmpty(String s) {
    if (s == null) {
      throw new NullPointerException();
    }
    if (s.isEmpty()) {
      throw new IllegalArgumentException();
    }
  }

  private ParamsUtils() {}
}
