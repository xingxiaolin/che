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
package org.eclipse.che.ide.api.workspace.model;

import java.util.Objects;
import org.eclipse.che.api.core.model.workspace.Warning;

/** Data object for {@link Warning}. */
public class WarningImpl implements Warning {

  private final int code;
  private final String message;

  public WarningImpl(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public WarningImpl(Warning warning) {
    this(warning.getCode(), warning.getMessage());
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof WarningImpl)) {
      return false;
    }
    final WarningImpl that = (WarningImpl) obj;
    return code == that.code && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + code;
    hash = 31 * hash + Objects.hashCode(message);
    return hash;
  }

  @Override
  public String toString() {
    return "Warning{" + "code=" + code + ", message='" + message + '\'' + '}';
  }
}
