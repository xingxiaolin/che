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

import org.eclipse.che.plugin.maven.shared.event.MavenPercentUndefinedEvent;

/** Implementation of the {@link MavenPercentUndefinedEvent}. */
public class MavenPercentUndefinedEventImpl extends MavenOutputEventImpl
    implements MavenPercentUndefinedEvent {
  private final boolean isPercentUndefined;

  public MavenPercentUndefinedEventImpl(boolean isPercentUndefined, TYPE type) {
    super(type);
    this.isPercentUndefined = isPercentUndefined;
  }

  @Override
  public boolean isPercentUndefined() {
    return isPercentUndefined;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MavenPercentUndefinedEventImpl)) return false;
    if (!super.equals(o)) return false;

    MavenPercentUndefinedEventImpl that = (MavenPercentUndefinedEventImpl) o;

    return isPercentUndefined == that.isPercentUndefined;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (isPercentUndefined ? 1 : 0);
    return result;
  }
}
