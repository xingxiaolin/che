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
package org.eclipse.che.api.workspace.server;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.eclipse.che.api.core.model.workspace.config.MachineConfig.MEMORY_LIMIT_ATTRIBUTE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.ValidationException;
import org.eclipse.che.api.core.model.workspace.Workspace;
import org.eclipse.che.api.core.model.workspace.WorkspaceConfig;
import org.eclipse.che.api.core.model.workspace.config.Command;
import org.eclipse.che.api.core.model.workspace.config.Environment;
import org.eclipse.che.api.core.model.workspace.config.MachineConfig;
import org.eclipse.che.api.core.model.workspace.config.Recipe;
import org.eclipse.che.api.core.model.workspace.config.Volume;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;

/**
 * Validator for {@link Workspace}.
 *
 * @author Yevhenii Voevodin
 */
@Singleton
public class WorkspaceValidator {

  /**
   * Must contain [3, 100] characters, first and last character is letter or digit, available
   * characters {A-Za-z0-9.-_}.
   */
  private static final Pattern WS_NAME =
      Pattern.compile("[a-zA-Z0-9][-_.a-zA-Z0-9]{1,98}[a-zA-Z0-9]");

  private static final Pattern VOLUME_NAME = Pattern.compile("[a-z][a-z0-9]{1,18}");
  private static final Pattern VOLUME_PATH = Pattern.compile("/.+");

  private final WorkspaceRuntimes runtimes;

  @Inject
  public WorkspaceValidator(WorkspaceRuntimes runtimes) {
    this.runtimes = runtimes;
  }

  /**
   * Checks whether given workspace configuration object is in application valid state, so it
   * provides enough data to be processed by internal components, and the data it provides is valid
   * so consistency is not violated.
   *
   * @param config configuration to validate
   * @throws ValidationException if any of validation constraints is violated
   * @throws NotFoundException when configuration contains a recipe with a type which is not
   *     supported by currently available workspace infrastructures
   * @throws ServerException when any other error occurs during environment validation
   */
  public void validateConfig(WorkspaceConfig config)
      throws ValidationException, NotFoundException, ServerException {
    // configuration object properties
    checkNotNull(config.getName(), "Workspace name required");
    check(
        WS_NAME.matcher(config.getName()).matches(),
        "Incorrect workspace name, it must be between 3 and 100 characters and may contain digits, "
            + "latin letters, underscores, dots, dashes and must start and end only with digits, "
            + "latin letters or underscores");

    // environments
    check(!isNullOrEmpty(config.getDefaultEnv()), "Workspace default environment name required");
    checkNotNull(config.getEnvironments(), "Workspace must contain at least one environment");
    check(
        config.getEnvironments().containsKey(config.getDefaultEnv()),
        "Workspace default environment configuration required");

    for (Environment environment : config.getEnvironments().values()) {
      checkNotNull(environment, "Environment must not be null");
      Recipe recipe = environment.getRecipe();
      checkNotNull(recipe, "Environment recipe must not be null");
      checkNotNull(recipe.getType(), "Environment recipe type must not be null");

      for (Entry<String, ? extends MachineConfig> machineEntry :
          environment.getMachines().entrySet()) {
        validateMachine(machineEntry.getKey(), machineEntry.getValue());
      }

      try {
        runtimes.validate(environment);
      } catch (InfrastructureException e) {
        throw new ServerException(e);
      }
    }

    // commands
    for (Command command : config.getCommands()) {
      check(
          !isNullOrEmpty(command.getName()),
          "Workspace %s contains command with null or empty name",
          config.getName());
      check(
          !isNullOrEmpty(command.getCommandLine()),
          "Command line required for command '%s' in workspace '%s'",
          command.getName(),
          config.getName());
    }

    // projects
    // TODO
  }

  /**
   * Checks whether workspace attributes are valid. The attribute is valid if it's key is not null &
   * not empty & is not prefixed with 'codenvy'.
   *
   * @param attributes the map to check
   * @throws ValidationException when attributes are not valid
   */
  public void validateAttributes(Map<String, String> attributes) throws ValidationException {
    for (String attributeName : attributes.keySet()) {
      // attribute name should not be empty and should not start with codenvy
      check(
          attributeName != null
              && !attributeName.trim().isEmpty()
              && !attributeName.toLowerCase().startsWith("codenvy"),
          "Attribute name '%s' is not valid",
          attributeName);
    }
  }

  private void validateMachine(String name, MachineConfig machine) throws ValidationException {
    String memoryAttribute = machine.getAttributes().get(MEMORY_LIMIT_ATTRIBUTE);
    if (memoryAttribute != null) {
      try {
        Long.parseLong(memoryAttribute);
      } catch (NumberFormatException e) {
        throw new ValidationException(
            format(
                "Value '%s' of attribute '%s' in machine '%s' is illegal",
                memoryAttribute, MEMORY_LIMIT_ATTRIBUTE, name));
      }
    }

    for (Entry<String, ? extends Volume> volumeEntry : machine.getVolumes().entrySet()) {
      String volumeName = volumeEntry.getKey();
      check(
          VOLUME_NAME.matcher(volumeName).matches(),
          "Volume name '%s' in machine '%s' is invalid",
          volumeName,
          name);
      Volume volume = volumeEntry.getValue();
      check(
          volume != null && !isNullOrEmpty(volume.getPath()),
          "Path of volume '%s' in machine '%s' is invalid. It should not be empty",
          volumeName,
          name);
      check(
          VOLUME_PATH.matcher(volume.getPath()).matches(),
          "Path '%s' of volume '%s' in machine '%s' is invalid. It should be absolute",
          volume.getPath(),
          volumeName,
          name);
    }
  }

  /**
   * Checks that object reference is not null, throws {@link ValidationException} in the case of
   * null {@code object} with given {@code message}.
   */
  private static void checkNotNull(Object object, String message) throws ValidationException {
    if (object == null) {
      throw new ValidationException(message);
    }
  }

  /**
   * Checks that expression is true, throws {@link ValidationException} otherwise.
   *
   * <p>Exception uses error message built from error message template and error message parameters.
   */
  private static void check(boolean expression, String fmt, Object... args)
      throws ValidationException {
    if (!expression) {
      throw new ValidationException(format(fmt, args));
    }
  }

  /**
   * Checks that expression is true, throws {@link ValidationException} otherwise.
   *
   * <p>Exception uses error message built from error message template and error message parameters.
   */
  private static void check(boolean expression, String message) throws ValidationException {
    if (!expression) {
      throw new ValidationException(message);
    }
  }
}
