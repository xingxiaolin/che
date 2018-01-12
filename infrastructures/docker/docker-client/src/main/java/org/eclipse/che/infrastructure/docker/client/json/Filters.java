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
package org.eclipse.che.infrastructure.docker.client.json;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of docker filters.
 *
 * @author Alexander Garagatyi
 */
public class Filters {

  private static final String LABEL_FILTER = "label";

  /** Creates filters with {@value #LABEL_FILTER} filter initialized to the given values. */
  public static Filters label(String... values) {
    return new Filters().withFilter(LABEL_FILTER, values);
  }

  private final Map<String, List<String>> filters = new HashMap<>();

  public Map<String, List<String>> getFilters() {
    final HashMap<String, List<String>> filtersCopy =
        Maps.newHashMapWithExpectedSize(filters.size());
    filters.forEach((s, strings) -> filtersCopy.put(s, Collections.unmodifiableList(strings)));

    return Collections.unmodifiableMap(filtersCopy);
  }

  public List<String> getFilter(String key) {
    return Collections.unmodifiableList(filters.get(key));
  }

  public Filters withFilter(String key, String... values) {
    filters.put(key, Arrays.asList(values));
    return this;
  }

  @Override
  public String toString() {
    return "Filters{" + "filters=" + filters + '}';
  }
}
