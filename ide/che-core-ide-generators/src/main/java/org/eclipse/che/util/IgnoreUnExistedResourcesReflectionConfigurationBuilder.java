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
package org.eclipse.che.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Reflections configuration builder that ignore resources that is not exists. It happens when ide
 * generators runs on generate-sources stage and ../target/classes folder is listed in class path
 * but not exists. Reflections print NPE for such folders. See more
 * https://github.com/ronmamo/reflections/issues/111
 *
 * @author Sergii Kabashniuk
 */
public final class IgnoreUnExistedResourcesReflectionConfigurationBuilder {

  private static ConfigurationBuilder configurationBuilder;

  static {
    configurationBuilder = ConfigurationBuilder.build();
    Collection<URL> classpath = new ArrayList<>();
    classpath.addAll(ClasspathHelper.forClassLoader());
    classpath.addAll(ClasspathHelper.forJavaClassPath());
    configurationBuilder.setUrls(classpath);

    configurationBuilder.setUrls(
        configurationBuilder
            .getUrls()
            .stream()
            .filter(
                input -> !"file".equals(input.getProtocol()) || new File(input.getFile()).exists())
            .collect(Collectors.toList()));
  }

  private IgnoreUnExistedResourcesReflectionConfigurationBuilder() {}

  /** @return Reflections ConfigurationBuilder that ignore not existing resources in class path. */
  public static ConfigurationBuilder getConfigurationBuilder() {
    return configurationBuilder;
  }
}
