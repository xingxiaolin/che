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
package org.eclipse.che.plugin.gdb.server.parser;

import static org.testng.Assert.assertEquals;

import org.eclipse.che.plugin.gdb.server.exception.GdbParseException;
import org.testng.annotations.Test;

/** @author Anatoliy Bazko */
public class GdbDirectoryTest {

  @Test
  public void testParse() throws Exception {
    GdbOutput gdbOutput =
        GdbOutput.of("Source directories searched: /home/tolusha/java/gdb/sources/1:$cdir:$cwd\n");

    GdbDirectory gdbDirectory = GdbDirectory.parse(gdbOutput);

    assertEquals(gdbDirectory.getDirectories(), "/home/tolusha/java/gdb/sources/1:$cdir:$cwd");
  }

  @Test(expectedExceptions = GdbParseException.class)
  public void testParseFail() throws Exception {
    GdbOutput gdbOutput =
        GdbOutput.of(
            "Warning: /home/tolusha/java/gdb/343: No such file or directory.\n"
                + "Source directories searched: /home/tolusha/java/gdb/343:$cdir:$cwd\n");
    GdbDirectory.parse(gdbOutput);
  }
}
