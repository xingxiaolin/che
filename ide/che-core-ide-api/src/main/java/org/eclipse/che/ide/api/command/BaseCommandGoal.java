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
package org.eclipse.che.ide.api.command;

import java.util.Objects;

/**
 * Base implementation of the {@link CommandGoal}.
 *
 * @author Artem Zatsarynnyi
 */
public class BaseCommandGoal implements CommandGoal {

  private final String id;

  public BaseCommandGoal(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof CommandGoal)) {
      return false;
    }

    CommandGoal other = (CommandGoal) o;

    return Objects.equals(getId(), other.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
