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
package org.eclipse.che.plugin.nodejsdbg.server.command;

import java.util.concurrent.Future;
import org.eclipse.che.plugin.nodejsdbg.server.NodeJsDebugProcess;
import org.eclipse.che.plugin.nodejsdbg.server.NodeJsProcessObserver;
import org.eclipse.che.plugin.nodejsdbg.server.exception.NodeJsDebuggerException;

/**
 * Any nodejs command to execute in debug.
 *
 * @see NodeJsDebugProcess
 * @see NodeJsDebugCommandsLibrary
 * @author Anatolii Bazko
 */
public interface NodeJsDebugCommand<T> extends NodeJsProcessObserver {

  /**
   * Executes command. Deferred result is returned.
   *
   * @param process the target process
   * @return the result of command execution
   * @throws NodeJsDebuggerException if execution failed
   */
  Future<T> execute(NodeJsDebugProcess process) throws NodeJsDebuggerException;
}
