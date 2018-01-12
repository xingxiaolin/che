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

import static java.util.Collections.singletonList;
import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.terminateVirtualMachineQuietly;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.che.api.debug.shared.model.DebuggerInfo;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.SuspendPolicy;
import org.eclipse.che.api.debug.shared.model.event.BreakpointActivatedEvent;
import org.eclipse.che.api.debug.shared.model.event.DebuggerEvent;
import org.eclipse.che.api.debug.shared.model.event.DisconnectEvent;
import org.eclipse.che.api.debug.shared.model.event.SuspendEvent;
import org.eclipse.che.api.debug.shared.model.impl.BreakpointImpl;
import org.eclipse.che.api.debug.shared.model.impl.LocationImpl;
import org.eclipse.che.api.debug.shared.model.impl.action.ResumeActionImpl;
import org.eclipse.che.api.debug.shared.model.impl.action.StartActionImpl;
import org.eclipse.che.api.debug.shared.model.impl.action.StepIntoActionImpl;
import org.eclipse.che.api.debug.shared.model.impl.action.StepOutActionImpl;
import org.eclipse.che.api.debug.shared.model.impl.action.StepOverActionImpl;
import org.eclipse.che.plugin.jdb.server.util.ProjectApiUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Anatolii Bazko */
public class JavaDebuggerTest {

  private JavaDebugger debugger;
  private BlockingQueue<DebuggerEvent> events;

  @BeforeClass
  protected void setUp() throws Exception {
    ProjectApiUtils.ensure();

    events = new ArrayBlockingQueue<>(10);
    Map<String, String> connectionProperties =
        ImmutableMap.of("host", "localhost", "port", System.getProperty("debug.port"));
    JavaDebuggerFactory factory = new JavaDebuggerFactory();
    debugger = (JavaDebugger) factory.create(connectionProperties, events::add);
  }

  @AfterClass
  public void tearDown() throws Exception {
    terminateVirtualMachineQuietly(debugger);
  }

  @Test(priority = 10)
  public void testGetInfo() throws Exception {
    DebuggerInfo info = debugger.getInfo();

    assertEquals(info.getHost(), "localhost");
    assertEquals(info.getPort(), Integer.parseInt(System.getProperty("debug.port")));

    assertNotNull(info.getName());
    assertNotNull(info.getVersion());
  }

  @Test(priority = 20)
  public void testStartDebugger() throws Exception {
    BreakpointImpl breakpoint =
        new BreakpointImpl(new LocationImpl("com.HelloWorld", 17), false, null);
    debugger.start(new StartActionImpl(singletonList(breakpoint)));

    DebuggerEvent debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof BreakpointActivatedEvent);

    debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);

    Location location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getLineNumber(), 17);
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
  }

  @Test(priority = 90)
  public void testSteps() throws Exception {
    debugger.deleteAllBreakpoints();

    debugger.addBreakpoint(new BreakpointImpl(new LocationImpl("com.HelloWorld", 20), false, null));

    assertTrue(events.take() instanceof BreakpointActivatedEvent);

    debugger.resume(new ResumeActionImpl());

    DebuggerEvent debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);
    Location location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
    assertEquals(location.getLineNumber(), 20);
    assertEquals(location.getExternalResourceId(), -1);
    assertEquals(location.getResourceProjectPath(), "/test");

    debugger.stepInto(new StepIntoActionImpl(SuspendPolicy.ALL));

    debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);
    location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
    assertEquals(location.getLineNumber(), 28);

    debugger.stepOut(new StepOutActionImpl(SuspendPolicy.ALL));

    debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);
    location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
    assertEquals(location.getLineNumber(), 20);

    debugger.stepOver(new StepOverActionImpl(SuspendPolicy.ALL));

    debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);
    location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
    assertEquals(location.getLineNumber(), 21);

    debugger.stepOver(new StepOverActionImpl(SuspendPolicy.ALL));

    debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);
    location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
    assertEquals(location.getLineNumber(), 23);

    debugger.stepOver(new StepOverActionImpl(SuspendPolicy.ALL));

    debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof SuspendEvent);
    location = ((SuspendEvent) debuggerEvent).getLocation();
    assertEquals(location.getTarget(), "/test/src/com/HelloWorld.java");
    assertEquals(location.getLineNumber(), 24);
  }

  @Test(priority = 120)
  public void testDisconnect() throws Exception {
    debugger.disconnect();

    DebuggerEvent debuggerEvent = events.take();
    assertTrue(debuggerEvent instanceof DisconnectEvent);
  }
}
