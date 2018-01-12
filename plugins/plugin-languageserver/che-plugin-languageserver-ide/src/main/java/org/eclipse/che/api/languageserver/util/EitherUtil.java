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
package org.eclipse.che.api.languageserver.util;

import com.google.gwt.json.client.JSONValue;
import org.eclipse.che.api.languageserver.shared.util.JsonDecision;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

/**
 * Code to be called by generated DTO classes to determine whether a json element matches the kind
 * of expected type in an {@link Either} field.
 *
 * @author Thomas Mäder
 */
public class EitherUtil {
  public static boolean matches(JSONValue element, JsonDecision[] classes) {
    for (JsonDecision cls : classes) {
      if (matches(element, cls)) {
        return true;
      }
    }
    return false;
  }

  private static boolean matches(JSONValue element, JsonDecision decision) {
    if (decision == JsonDecision.LIST) {
      return element.isArray() != null;
    }
    if (decision == JsonDecision.BOOLEAN) {
      return element.isBoolean() != null;
    }
    if (decision == JsonDecision.NUMBER) {
      return element.isNumber() != null;
    }
    if (decision == JsonDecision.STRING) {
      return element.isString() != null;
    }
    return element.isObject() != null;
  }
}
