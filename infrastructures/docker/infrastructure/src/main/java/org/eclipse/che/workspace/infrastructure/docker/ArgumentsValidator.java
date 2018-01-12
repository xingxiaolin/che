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
package org.eclipse.che.workspace.infrastructure.docker;

import static java.lang.String.format;

import org.eclipse.che.api.core.ValidationException;

/** @author Alexander Garagatyi */
public class ArgumentsValidator {
  public static void checkArgument(boolean expression, String error) throws ValidationException {
    if (!expression) {
      throw new ValidationException(error);
    }
  }

  public static void checkArgument(
      boolean expression, String errorMessageTemplate, Object... errorMessageParams)
      throws ValidationException {
    if (!expression) {
      throw new ValidationException(format(errorMessageTemplate, errorMessageParams));
    }
  }

  private ArgumentsValidator() {}
}
