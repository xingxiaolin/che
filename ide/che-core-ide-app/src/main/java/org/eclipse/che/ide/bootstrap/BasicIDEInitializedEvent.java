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
package org.eclipse.che.ide.bootstrap;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when Basic IDE is initialized. Intended to be used exclusively by Basic IDE's components to
 * be able to start it's initialization.
 *
 * <p>Basically event is useful for components which depend on {@link
 * org.eclipse.che.ide.api.app.AppContext}.
 */
public class BasicIDEInitializedEvent extends GwtEvent<BasicIDEInitializedEvent.Handler> {

  public static final Type<BasicIDEInitializedEvent.Handler> TYPE = new Type<>();

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onIDEInitialized(this);
  }

  public interface Handler extends EventHandler {
    void onIDEInitialized(BasicIDEInitializedEvent event);
  }
}
