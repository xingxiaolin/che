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
package org.eclipse.che.multiuser.resource.api.usage.tracker;

import static java.lang.String.format;

import java.util.Map;
import javax.inject.Inject;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.ValidationException;
import org.eclipse.che.api.core.model.workspace.Runtime;
import org.eclipse.che.api.core.model.workspace.config.Environment;
import org.eclipse.che.api.core.model.workspace.config.MachineConfig;
import org.eclipse.che.api.core.model.workspace.runtime.Machine;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironment;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironmentFactory;

/**
 * Helps to calculate amount of RAM defined in {@link Environment environment}
 *
 * @author Sergii Leschenko
 * @author Anton Korneta
 */
public class EnvironmentRamCalculator {
  private static final long BYTES_TO_MEGABYTES_DIVIDER = 1024L * 1024L;

  private final Map<String, InternalEnvironmentFactory> environmentFactories;

  @Inject
  public EnvironmentRamCalculator(Map<String, InternalEnvironmentFactory> environmentFactories) {
    this.environmentFactories = environmentFactories;
  }

  /**
   * Parses (and fetches if needed) recipe of environment and sums RAM size of all machines in
   * environment in megabytes.
   */
  public long calculate(Environment environment) throws ServerException {
    try {
      return getInternalEnvironment(environment)
              .getMachines()
              .values()
              .stream()
              .mapToLong(
                  m -> Long.parseLong(m.getAttributes().get(MachineConfig.MEMORY_LIMIT_ATTRIBUTE)))
              .sum()
          / BYTES_TO_MEGABYTES_DIVIDER;
    } catch (InfrastructureException | ValidationException | NotFoundException ex) {
      throw new ServerException(ex.getMessage(), ex);
    }
  }

  /**
   * Calculates summary RAM of given {@link Runtime}.
   *
   * @return summary RAM of all machines in runtime
   */
  public long calculate(Runtime runtime) {
    return runtime
            .getMachines()
            .values()
            .stream()
            .mapToLong(m -> Long.parseLong(m.getAttributes().get(Machine.MEMORY_LIMIT_ATTRIBUTE)))
            .sum()
        / BYTES_TO_MEGABYTES_DIVIDER;
  }

  private InternalEnvironment getInternalEnvironment(Environment environment)
      throws InfrastructureException, ValidationException, NotFoundException {
    final String recipeType = environment.getRecipe().getType();
    final InternalEnvironmentFactory factory = environmentFactories.get(recipeType);
    if (factory == null) {
      throw new NotFoundException(
          format("InternalEnvironmentFactory is not configured for recipe type: '%s'", recipeType));
    }
    return factory.create(environment);
  }
}
