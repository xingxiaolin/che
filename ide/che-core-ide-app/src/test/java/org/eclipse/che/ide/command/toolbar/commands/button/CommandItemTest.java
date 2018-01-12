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
package org.eclipse.che.ide.command.toolbar.commands.button;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.eclipse.che.ide.api.command.CommandImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link CommandItem}. */
@RunWith(MockitoJUnitRunner.class)
public class CommandItemTest {

  private static final String COMMAND_NAME = "cmd";

  @Mock private CommandImpl command;

  @InjectMocks private CommandItem item;

  @Before
  public void setUp() throws Exception {
    when(command.getName()).thenReturn(COMMAND_NAME);
  }

  @Test
  public void testGetName() throws Exception {
    assertEquals(COMMAND_NAME, item.getName());
  }
}
