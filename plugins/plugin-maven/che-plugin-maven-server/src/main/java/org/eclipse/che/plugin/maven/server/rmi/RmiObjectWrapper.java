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
package org.eclipse.che.plugin.maven.server.rmi;

import java.rmi.RemoteException;

/** @author Evgen Vidolob */
public abstract class RmiObjectWrapper<T> {
  private T wrapped;

  protected RmiObjectWrapper() {}

  protected synchronized T getWrapped() {
    return wrapped;
  }

  protected synchronized T getOrCreateWrappedObject() throws RemoteException {
    if (wrapped == null) {
      wrapped = create();
      wrappedCreated();
    }

    return wrapped;
  }

  protected void wrappedCreated() throws RemoteException {}

  protected abstract T create() throws RemoteException;

  protected synchronized void cleanUp() {
    wrapped = null;
  }

  protected synchronized void onError() {
    cleanUp();
  }
}
