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
package org.eclipse.che.api.core.websocket.commons;

/**
 * Used as entry point for a web socket protocol message consumers.
 *
 * @author Dmitry Kuleshov
 */
public interface WebSocketMessageReceiver {
  /**
   * Receives a message by a a web socket protocol.
   *
   * @param endpointId identifier of an endpoint known to an transmitter implementation
   * @param message plain text message
   */
  void receive(String endpointId, String message);
}
