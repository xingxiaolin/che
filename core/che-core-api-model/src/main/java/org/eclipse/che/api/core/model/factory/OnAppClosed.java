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
package org.eclipse.che.api.core.model.factory;

import java.util.List;

/**
 * Defines IDE look and feel on application closed event.
 *
 * @author Anton Korneta
 */
public interface OnAppClosed {

  /** Returns actions for current event. */
  List<? extends Action> getActions();
}
