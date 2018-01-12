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
import org.eclipse.che.plugin.gdb.server.exception.GdbParseException;

/**
 * 'ptype' command parser.
 *
 * @author Anatoliy Bazko
 */
public class GdbPType {

  private static final Pattern GDB_ARGS = Pattern.compile("type = (.*)");

  private final String type;

  public GdbPType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  /** Factory method. */
  public static GdbPType parse(GdbOutput gdbOutput) throws GdbParseException {
    String output = gdbOutput.getOutput();

    Matcher matcher = GDB_ARGS.matcher(output);
    if (matcher.find()) {
      String type = matcher.group(1);
      return new GdbPType(type);
    }

    throw new GdbParseException(GdbPrint.class, output);
  }
}
