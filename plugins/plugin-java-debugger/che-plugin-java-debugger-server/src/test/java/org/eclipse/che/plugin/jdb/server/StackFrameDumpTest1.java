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
import static org.eclipse.che.api.debugger.server.DtoConverter.asDto;
import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.ensureSuspendAtDesiredLocation;
import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.startJavaDebugger;
import static org.eclipse.che.plugin.jdb.server.util.JavaDebuggerUtils.terminateVirtualMachineQuietly;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.che.api.debug.shared.dto.FieldDto;
import org.eclipse.che.api.debug.shared.dto.LocationDto;
import org.eclipse.che.api.debug.shared.dto.MethodDto;
import org.eclipse.che.api.debug.shared.dto.StackFrameDumpDto;
import org.eclipse.che.api.debug.shared.dto.VariableDto;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.ThreadState;
import org.eclipse.che.api.debug.shared.model.event.DebuggerEvent;
import org.eclipse.che.api.debug.shared.model.impl.BreakpointImpl;
import org.eclipse.che.api.debug.shared.model.impl.LocationImpl;
import org.eclipse.che.api.debugger.server.exceptions.DebuggerException;
import org.eclipse.che.plugin.jdb.server.util.ProjectApiUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test ThreadDump when all threads are suspended.
 *
 * @author Anatolii Bazko
 */
public class StackFrameDumpTest1 {
  private JavaDebugger debugger;
  private BlockingQueue<DebuggerEvent> callback = new ArrayBlockingQueue<>(10);

  @BeforeClass
  public void setUp() throws Exception {
    ProjectApiUtils.ensure();

    Location location =
        new LocationImpl(
            "/test/src/org/eclipse/StackFrameDumpTest1.java", 25, false, -1, "/test", null, -1);
    debugger = startJavaDebugger(new BreakpointImpl(location), callback);

    ensureSuspendAtDesiredLocation(location, callback);
  }

  @AfterClass
  public void tearDown() throws Exception {
    if (debugger != null) {
      terminateVirtualMachineQuietly(debugger);
    }
  }

  @Test
  public void shouldGetStackFrameDump() throws Exception {
    Optional<ThreadState> main =
        debugger.getThreadDump().stream().filter(t -> t.getName().equals("main")).findAny();
    assertTrue(main.isPresent());

    ThreadState mainThread = main.get();
    assertEquals(mainThread.getFrames().size(), 3);

    validateFrame0(mainThread.getId());
    validateFrame1(mainThread.getId());
    validateFrame2(mainThread.getId());
  }

  private void validateFrame0(long threadId) throws DebuggerException {
    StackFrameDumpDto stackFrame = asDto(debugger.getStackFrameDump(threadId, 0));

    LocationDto location = stackFrame.getLocation();
    assertEquals(location.getLineNumber(), 25);
    assertEquals(location.getTarget(), "/test/src/org/eclipse/StackFrameDumpTest1.java");

    MethodDto method = location.getMethod();
    assertEquals(method.getName(), "do2");

    List<VariableDto> arguments = method.getArguments();
    assertEquals(arguments.size(), 1);

    VariableDto variable = arguments.get(0);
    assertEquals(variable.getName(), "str");
    assertEquals(variable.getType(), "java.lang.String");
    assertEquals(variable.getVariablePath().getPath(), singletonList("str"));
    assertFalse(variable.isPrimitive());
    assertNull(variable.getValue());

    List<VariableDto> variables = stackFrame.getVariables();
    assertEquals(variables.size(), 1);

    variable = variables.get(0);
    assertEquals(variable.getName(), "str");
    assertEquals(variable.getType(), "java.lang.String");
    assertEquals(variable.getVariablePath().getPath(), singletonList("str"));
    assertFalse(variable.isPrimitive());
    assertEquals(variable.getValue().getString(), "\"2\"");
    assertTrue(variable.getValue().getVariables().isEmpty());

    List<FieldDto> fields = stackFrame.getFields();
    assertEquals(fields.size(), 1);

    FieldDto field = fields.get(0);
    assertEquals(field.getName(), "v");
    assertEquals(field.getType(), "java.lang.String");
    assertEquals(field.getVariablePath().getPath(), ImmutableList.of("static", "v"));
    assertFalse(field.isPrimitive());
    assertTrue(field.isIsStatic());
    assertFalse(field.isIsFinal());
    assertFalse(field.isIsTransient());
    assertEquals(field.getValue().getString(), "\"something\"");
    assertTrue(field.getValue().getVariables().isEmpty());
  }

  private void validateFrame1(long threadId) throws DebuggerException {
    StackFrameDumpDto stackFrame = asDto(debugger.getStackFrameDump(threadId, 1));

    LocationDto location = stackFrame.getLocation();
    assertEquals(location.getLineNumber(), 21);
    assertEquals(location.getTarget(), "/test/src/org/eclipse/StackFrameDumpTest1.java");

    MethodDto method = location.getMethod();
    assertEquals(method.getName(), "do1");

    List<VariableDto> arguments = method.getArguments();
    assertEquals(arguments.size(), 1);

    VariableDto variable = arguments.get(0);
    assertEquals(variable.getName(), "i");
    assertEquals(variable.getType(), "int");
    assertEquals(variable.getVariablePath().getPath(), singletonList("i"));
    assertTrue(variable.isPrimitive());
    assertNull(variable.getValue());

    List<VariableDto> variables = stackFrame.getVariables();
    assertEquals(variables.size(), 2);

    variable = variables.get(0);
    assertEquals(variable.getName(), "i");
    assertEquals(variable.getType(), "int");
    assertEquals(variable.getVariablePath().getPath(), singletonList("i"));
    assertTrue(variable.isPrimitive());
    assertEquals(variable.getValue().getString(), "1");
    assertTrue(variable.getValue().getVariables().isEmpty());

    variable = variables.get(1);
    assertEquals(variable.getName(), "j");
    assertEquals(variable.getType(), "int");
    assertEquals(variable.getVariablePath().getPath(), singletonList("j"));
    assertTrue(variable.isPrimitive());
    assertEquals(variable.getValue().getString(), "1");
    assertTrue(variable.getValue().getVariables().isEmpty());

    List<FieldDto> fields = stackFrame.getFields();
    assertEquals(fields.size(), 1);

    FieldDto field = fields.get(0);
    assertEquals(field.getName(), "v");
    assertEquals(field.getType(), "java.lang.String");
    assertEquals(field.getVariablePath().getPath(), ImmutableList.of("static", "v"));
    assertFalse(field.isPrimitive());
    assertTrue(field.isIsStatic());
    assertFalse(field.isIsFinal());
    assertFalse(field.isIsTransient());
    assertEquals(field.getValue().getString(), "\"something\"");
    assertTrue(field.getValue().getVariables().isEmpty());
  }

  private void validateFrame2(long threadId) throws DebuggerException {
    StackFrameDumpDto stackFrame = asDto(debugger.getStackFrameDump(threadId, 2));

    LocationDto location = stackFrame.getLocation();
    assertEquals(location.getLineNumber(), 16);
    assertEquals(location.getTarget(), "/test/src/org/eclipse/StackFrameDumpTest1.java");

    MethodDto method = location.getMethod();
    assertEquals(method.getName(), "main");

    List<VariableDto> arguments = method.getArguments();
    assertEquals(arguments.size(), 1);

    VariableDto variable = arguments.get(0);
    assertEquals(variable.getName(), "args");
    assertEquals(variable.getType(), "java.lang.String[]");
    assertEquals(variable.getVariablePath().getPath(), singletonList("args"));
    assertFalse(variable.isPrimitive());
    assertNull(variable.getValue());

    List<VariableDto> variables = stackFrame.getVariables();
    assertEquals(variables.size(), 1);

    variable = variables.get(0);
    assertEquals(variable.getName(), "args");
    assertEquals(variable.getType(), "java.lang.String[]");
    assertEquals(variable.getVariablePath().getPath(), singletonList("args"));
    assertFalse(variable.isPrimitive());
    assertTrue(variable.getValue().getString().contains("java.lang.String[0]"));
    assertTrue(variable.getValue().getVariables().isEmpty());

    List<FieldDto> fields = stackFrame.getFields();
    assertEquals(fields.size(), 1);

    FieldDto field = fields.get(0);
    assertEquals(field.getName(), "v");
    assertEquals(field.getType(), "java.lang.String");
    assertEquals(field.getVariablePath().getPath(), ImmutableList.of("static", "v"));
    assertFalse(field.isPrimitive());
    assertTrue(field.isIsStatic());
    assertFalse(field.isIsFinal());
    assertFalse(field.isIsTransient());
    assertEquals(field.getValue().getString(), "\"something\"");
    assertTrue(field.getValue().getVariables().isEmpty());
  }
}
