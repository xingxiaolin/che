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
package org.eclipse.che.ide.ext.java.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Tests for {@link JavaUtils} */
public class JavaUtilsTest {
  @Test
  public void shouldValidatePackageNameWithCorrectContent() throws Exception {
    assertTrue(JavaUtils.isValidPackageName("Package_name"));
  }

  @Test
  public void shouldInvalidatePackageNameWithWhiteSpaces() throws Exception {
    assertFalse(JavaUtils.isValidPackageName("Package name"));
  }

  @Test
  public void shouldInvalidatePackageNameWithMinuses() throws Exception {
    assertFalse(JavaUtils.isValidPackageName("Package-name"));
  }

  @Test
  public void shouldInvalidatePackageNameStartingWithNumbers() throws Exception {
    assertFalse(JavaUtils.isValidPackageName("1Package_name"));
  }

  @Test
  public void shouldInvalidatePackageNameStartingWithSpaces() throws Exception {
    assertFalse(JavaUtils.isValidPackageName(" Package_name"));
  }

  @Test
  public void shouldValidateClassNameWithCorrectContent() throws Exception {
    assertTrue(JavaUtils.isValidCompilationUnitName("Class_name"));
  }

  @Test
  public void shouldInvalidateClassNameWithSpecialSymbols() throws Exception {
    assertFalse(JavaUtils.isValidCompilationUnitName("&%$#"));
  }

  @Test
  public void shouldInvalidateClassNameWithMinuses() throws Exception {
    assertFalse(JavaUtils.isValidCompilationUnitName("Class-name"));
  }

  @Test
  public void shouldInvalidateClassNameStartingWithNumbers() throws Exception {
    assertFalse(JavaUtils.isValidCompilationUnitName("1Class_name"));
  }

  @Test
  public void shouldInvalidateClassNameStartingWithSpaces() throws Exception {
    assertFalse(JavaUtils.isValidCompilationUnitName(" Class_name"));
  }
}
