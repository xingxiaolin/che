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
package org.eclipse.che.plugin.maven.shared.impl;

import org.eclipse.che.plugin.maven.shared.event.MavenStartStopEvent;

/** Implementation of the {@link MavenStartStopEvent}. */
public class MavenStartStopEventImpl extends MavenOutputEventImpl implements MavenStartStopEvent {
  private final boolean isStart;

  public MavenStartStopEventImpl(boolean isStart, TYPE type) {
    super(type);
    this.isStart = isStart;
  }

  @Override
  public boolean isStart() {
    return isStart;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MavenStartStopEventImpl)) return false;
    if (!super.equals(o)) return false;

    MavenStartStopEventImpl that = (MavenStartStopEventImpl) o;

    return isStart == that.isStart;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (isStart ? 1 : 0);
    return result;
  }
}
