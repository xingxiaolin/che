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
package org.eclipse.che.plugin.nodejsdbg.server.parser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import org.eclipse.che.plugin.nodejsdbg.server.NodeJsOutput;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** @author Anatolii Bazko */
public class NodeJsScriptsParserTest {

  private NodeJsScriptsParser parser;

  @BeforeMethod
  public void setUp() throws Exception {
    parser = new NodeJsScriptsParser();
  }

  @Test
  public void testParseScriptCommand() throws Exception {
    NodeJsOutput output = NodeJsOutput.of("  35: bootstrap_node.js\n" + "* 63: app.js\n");

    assertTrue(parser.match(output));

    Map<Integer, String> scripts = parser.parse(output).getAll();

    assertEquals(scripts.size(), 2);
    assertEquals(scripts.get(35), "bootstrap_node.js");
    assertEquals(scripts.get(63), "app.js");
  }
}
