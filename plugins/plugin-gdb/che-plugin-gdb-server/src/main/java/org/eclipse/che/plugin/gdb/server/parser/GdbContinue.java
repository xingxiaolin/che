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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.che.api.debug.shared.model.Breakpoint;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.impl.BreakpointImpl;
import org.eclipse.che.api.debug.shared.model.impl.LocationImpl;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.plugin.gdb.server.exception.GdbParseException;

/**
 * 'run' command parser.
 *
 * @author Anatoliy Bazko
 */
public class GdbContinue {

  private static final Pattern GDB_BREAKPOINT = Pattern.compile("Breakpoint .* at (.*):([0-9]*).*");

  private final Breakpoint breakpoint;

  public GdbContinue(Breakpoint breakpoint) {
    this.breakpoint = breakpoint;
  }

  @Nullable
  public Breakpoint getBreakpoint() {
    return breakpoint;
  }

  /** Factory method. */
  public static GdbContinue parse(GdbOutput gdbOutput) throws GdbParseException {
    String output = gdbOutput.getOutput();

    for (String line : output.split("\n")) {
      Matcher matcher = GDB_BREAKPOINT.matcher(line);
      if (matcher.find()) {
        String file = matcher.group(1);
        String lineNumber = matcher.group(2);

        Location location = new LocationImpl(file, Integer.parseInt(lineNumber));
        return new GdbContinue(new BreakpointImpl(location));
      }
    }

    return new GdbContinue(null);
  }
}
