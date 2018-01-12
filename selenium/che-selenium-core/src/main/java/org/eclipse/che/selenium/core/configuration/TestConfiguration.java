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
package org.eclipse.che.selenium.core.configuration;

import java.util.Map;

/**
 * Represents configuration of testing framework
 *
 * @author Sergii Kabashniuk
 */
public interface TestConfiguration {

  /**
   * Test if configuration contains parameter.
   *
   * @param key configuration key.
   * @return - true if key is configured.
   */
  boolean isConfigured(String key);

  /**
   * @param key - configuration key.
   * @return String based value of configuration property.
   * @throws ConfigurationException if kys is not configured
   */
  String getString(String key) throws ConfigurationException;

  /**
   * @param key - configuration key.
   * @return Boolean based value of configuration property.
   * @throws ConfigurationException if kys is not configured
   */
  Boolean getBoolean(String key) throws ConfigurationException;

  /**
   * @param key - configuration key.
   * @return Integer based value of configuration property.
   * @throws ConfigurationException if kys is not configured
   */
  Integer getInt(String key) throws ConfigurationException;

  /**
   * @param key - configuration key.
   * @return Long based value of configuration property.
   * @throws ConfigurationException if kys is not configured
   */
  Long getLong(String key) throws ConfigurationException;

  /** @return all configuration parameters. */
  Map<String, String> getMap();

  /**
   * @param keyPrefix - filter all properties with given prefix
   * @return - key/value map of configuration.
   */
  Map<String, String> getMap(String keyPrefix);
}
