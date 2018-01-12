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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/** @author Mykola Morhun */
public class KillContainerParamsTest {

  private static final String CONTAINER = "container";
  private static final int SIGNAL = 9;

  private KillContainerParams killContainerParams;

  @Test
  public void shouldCreateParamsObjectWithRequiredParameters() {
    killContainerParams = KillContainerParams.create(CONTAINER);

    assertEquals(killContainerParams.getContainer(), CONTAINER);

    assertNull(killContainerParams.getSignal());
  }

  @Test
  public void shouldCreateParamsObjectWithAllPossibleParameters() {
    killContainerParams = KillContainerParams.create(CONTAINER).withSignal(SIGNAL);

    assertEquals(killContainerParams.getContainer(), CONTAINER);
    assertTrue(killContainerParams.getSignal() == SIGNAL);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfContainerRequiredParameterIsNull() {
    killContainerParams = KillContainerParams.create(null);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfContainerRequiredParameterResetWithNull() {
    killContainerParams.withContainer(null);
  }
}
