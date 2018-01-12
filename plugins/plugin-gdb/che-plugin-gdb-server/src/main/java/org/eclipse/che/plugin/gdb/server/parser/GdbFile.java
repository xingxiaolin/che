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
 * 'file' command parser.
 *
 * @author Anatoliy Bazko
 */
public class GdbFile {

  private static final Pattern GDB_FILE = Pattern.compile("Reading symbols from (.*)...done.*");

  private final String file;

  private GdbFile(String file) {
    this.file = file;
  }

  public String getFile() {
    return file;
  }

  /** Factory method. */
  public static GdbFile parse(GdbOutput gdbOutput) throws GdbParseException {
    String output = gdbOutput.getOutput();

    Matcher matcher = GDB_FILE.matcher(output);
    if (matcher.find()) {
      String file = matcher.group(1);
      return new GdbFile(file);
    }

    throw new GdbParseException(GdbFile.class, output);
  }
}
