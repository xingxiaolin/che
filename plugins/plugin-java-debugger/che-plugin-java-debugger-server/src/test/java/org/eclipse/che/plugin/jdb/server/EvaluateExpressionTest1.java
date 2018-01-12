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
package org.eclipse.che.plugin.jdb.server;

import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.ensureSuspendAtDesiredLocation;
import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.startJavaDebugger;
import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.terminateVirtualMachineQuietly;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.ThreadState;
import org.eclipse.che.api.debug.shared.model.event.DebuggerEvent;
import org.eclipse.che.api.debug.shared.model.impl.BreakpointImpl;
import org.eclipse.che.api.debug.shared.model.impl.LocationImpl;
import org.eclipse.che.plugin.jdb.server.util.ProjectApiUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ThreadDump when all threads are suspended.
 *
 * @author Anatolii Bazko
 */
public class EvaluateExpressionTest1 {

  private JavaDebugger debugger;
  private BlockingQueue<DebuggerEvent> debuggerEvents;

  @BeforeClass
  public void setUp() throws Exception {
    ProjectApiUtils.ensure();

    Location location =
        new LocationImpl(
            "/test/src/org/eclipse/EvaluateExpressionTest1.java", 21, false, -1, "/test", null, -1);
    debuggerEvents = new ArrayBlockingQueue<>(10);
    debugger = startJavaDebugger(new BreakpointImpl(location), debuggerEvents);
    ensureSuspendAtDesiredLocation(location, debuggerEvents);
  }

  @AfterClass
  public void tearDown() throws Exception {
    if (debugger != null) {
      terminateVirtualMachineQuietly(debugger);
    }
  }

  @Test(dataProvider = "evaluateExpression")
  public void shouldEvaluateExpression(String expression, String expectedResult, int frameIndex)
      throws Exception {
    Optional<ThreadState> main =
        debugger.getThreadDump().stream().filter(t -> t.getName().equals("main")).findAny();
    assertTrue(main.isPresent());

    ThreadState mainThread = main.get();

    String actualResult = debugger.evaluate(expression, mainThread.getId(), frameIndex);
    assertEquals(actualResult, expectedResult);
  }

  @DataProvider(name = "evaluateExpression")
  public static Object[][] getEvaluateExpression() {
    return new Object[][] {
      {"i+1", "3", 0},
      {"2+2", "4", 0},
      {"\"hello\"+\"world\"", "\"helloworld\"", 0},
      {"i+1", "2", 1}
    };
  }
}
