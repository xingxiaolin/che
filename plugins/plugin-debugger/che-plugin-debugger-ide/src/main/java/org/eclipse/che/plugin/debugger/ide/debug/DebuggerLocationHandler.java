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
package org.eclipse.che.plugin.debugger.ide.debug;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.ide.api.resources.VirtualFile;

/**
 * Handles resources is being debugged.
 *
 * @see Location#getTarget()
 * @see Location#getLineNumber()
 * @author Anatoliy Bazko
 */
public interface DebuggerLocationHandler {

  /** Indicates if handler can deal with the given location. */
  boolean isSuitedFor(Location location);

  /**
   * Opens resource is being debugged and scrolls to the position {@link Location#getLineNumber()}.
   * If resource has been found then {@link AsyncCallback#onSuccess(Object)} must be invoked and
   * {@link AsyncCallback#onFailure(Throwable)} otherwise.
   *
   * @param location the location of the resource
   * @param callback the callback
   */
  void open(Location location, AsyncCallback<VirtualFile> callback);

  /**
   * Finds resource denoted by {@code #location}. If resource has been found then {@link
   * AsyncCallback#onSuccess(Object)} must be invoked and {@link AsyncCallback#onFailure(Throwable)}
   * otherwise.
   *
   * @param location the location of the resource
   * @param callback the callback
   */
  void find(Location location, AsyncCallback<VirtualFile> callback);
}
