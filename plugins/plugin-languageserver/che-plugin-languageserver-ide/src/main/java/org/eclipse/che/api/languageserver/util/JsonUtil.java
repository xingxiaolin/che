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

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * Utility to convert stuff that is not statically typed in lsp4j (java.lang.Object)
 *
 * @author Thomas Mäder
 */
public class JsonUtil {
  public static JSONValue convertToJson(Object value) {
    if (value instanceof Enum) {
      return new JSONString(((Enum<?>) value).name());
    } else if (value instanceof String) {
      return new JSONString((String) value);
    } else if (value instanceof Number) {
      return new JSONNumber(((Number) value).doubleValue());
    } else if (value instanceof Boolean) {
      return JSONBoolean.getInstance((boolean) value);
    } else if (value instanceof JsonSerializable) {
      return ((JsonSerializable) value).toJsonElement();
    } else if (value instanceof JSONValue) {
      return (JSONValue) value;
    }
    throw new RuntimeException("Unexpected runtime value: " + value);
  }
}
