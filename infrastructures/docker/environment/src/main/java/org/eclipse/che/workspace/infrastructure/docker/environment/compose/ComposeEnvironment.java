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
package org.eclipse.che.workspace.infrastructure.docker.environment.compose;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.che.api.core.model.workspace.Warning;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironment;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.api.workspace.server.spi.environment.InternalRecipe;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.model.ComposeService;

/** @author Sergii Leshchenko */
public class ComposeEnvironment extends InternalEnvironment {

  public static final String TYPE = "compose";

  private String version;
  private LinkedHashMap<String, ComposeService> services;

  ComposeEnvironment(
      String version,
      LinkedHashMap<String, ComposeService> services,
      InternalRecipe recipe,
      Map<String, InternalMachineConfig> machines,
      List<Warning> warnings) {
    super(recipe, machines, warnings);
    this.version = version;
    this.services = services;
  }

  public String getVersion() {
    return version;
  }

  /** Returns ordered services. */
  public LinkedHashMap<String, ComposeService> getServices() {
    if (services == null) {
      services = new LinkedHashMap<>();
    }
    return services;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ComposeEnvironment)) {
      return false;
    }
    final ComposeEnvironment that = (ComposeEnvironment) obj;
    return Objects.equals(version, that.version)
        && Objects.equals(getServices(), that.getServices())
        && Objects.equals(getRecipe(), that.getRecipe())
        && Objects.equals(getMachines(), that.getMachines())
        && Objects.equals(getWarnings(), that.getWarnings());
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, getServices(), getMachines(), getRecipe(), getWarnings());
  }

  @Override
  public String toString() {
    return "ComposeEnvironment{"
        + "version='"
        + version
        + '\''
        + ", services="
        + getServices()
        + ", machines="
        + getMachines()
        + ", recipe="
        + getRecipe()
        + ", warnings="
        + getWarnings()
        + '}';
  }
}
