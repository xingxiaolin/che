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

import static java.lang.String.format;
import static java.util.Arrays.fill;
import static org.eclipse.che.api.core.model.workspace.config.MachineConfig.MEMORY_LIMIT_ATTRIBUTE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.api.core.ValidationException;
import org.eclipse.che.api.installer.server.InstallerRegistry;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.api.workspace.server.spi.environment.MachineConfigsValidator;
import org.eclipse.che.api.workspace.server.spi.environment.RecipeRetriever;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.deserializer.EnvironmentDeserializer;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.model.ComposeRecipe;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.model.ComposeService;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test deserialization field {@link ComposeService#environment} by {@link EnvironmentDeserializer}
 * in the {@link ComposeEnvironmentFactory}.
 *
 * @author Dmytro Nochevnov
 */
@Listeners(MockitoTestNGListener.class)
public class ComposeEnvironmentVariableTest {

  private static final long DEFAULT_RAM_LIMIT_MB = 2048;

  private static final String MACHINE_NAME_1 = "machine1";
  private static final String MACHINE_NAME_2 = "machine2";

  @Mock InstallerRegistry installerRegistry;
  @Mock RecipeRetriever recipeRetriever;
  @Mock MachineConfigsValidator machinesValidator;
  @Mock ComposeEnvironmentValidator composeValidator;
  @Mock ComposeServicesStartStrategy startStrategy;

  private ComposeEnvironmentFactory factory;

  @BeforeMethod
  public void setup() {
    factory =
        new ComposeEnvironmentFactory(
            installerRegistry,
            recipeRetriever,
            machinesValidator,
            composeValidator,
            startStrategy,
            DEFAULT_RAM_LIMIT_MB);
  }

  @Test(dataProvider = "correctContentTestData")
  public void testCorrectContentParsing(String content, Map<String, String> expected)
      throws Exception {
    ComposeRecipe composeRecipe = factory.doParse(content);

    // then
    assertEquals(composeRecipe.getServices().get("dev-machine").getEnvironment(), expected);
  }

  @DataProvider
  public Object[][] correctContentTestData() {
    return new Object[][] {
      // dictionary type environment
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "    RACK_ENV: development\n"
            + "    SHOW: 'true'",
        ImmutableMap.of(
            "RACK_ENV", "development",
            "SHOW", "true")
      },

      // dictionary format, value of variable is empty
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   MYSQL_ROOT_PASSWORD: \"\"",
        ImmutableMap.of("MYSQL_ROOT_PASSWORD", "")
      },

      // dictionary format, value of variable contains colon sign
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   VAR : val:1",
        ImmutableMap.of("VAR", "val:1")
      },

      // array type environment
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   - MYSQL_ROOT_PASSWORD=root\n"
            + "   - MYSQL_DATABASE=db",
        ImmutableMap.of(
            "MYSQL_ROOT_PASSWORD", "root",
            "MYSQL_DATABASE", "db")
      },

      // array format, value of variable contains equal sign
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   - VAR=val=1",
        ImmutableMap.of("VAR", "val=1")
      },

      // array format, empty value of variable
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   - VAR= ",
        ImmutableMap.of("VAR", "")
      },

      // empty environment
      {
        "services: \n" + " dev-machine: \n" + "  image: codenvy/ubuntu_jdk8\n" + "  environment:",
        ImmutableMap.of()
      },
    };
  }

  @Test(dataProvider = "incorrectContentTestData")
  public void shouldThrowError(String content, String errorPattern) throws Exception {
    try {
      factory.doParse(content);
    } catch (ValidationException e) {
      assertTrue(
          e.getMessage().matches(errorPattern),
          format(
              "Actual error message \"%s\" doesn't match regex \"%s\" for content \"%s\"",
              e.getMessage(), errorPattern, content));
      return;
    }

    fail(format("Content \"%s\" should throw IllegalArgumentException", content));
  }

  @DataProvider
  public Object[][] incorrectContentTestData() {
    return new Object[][] {
      // unsupported type of environment
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   true",
        "Parsing of environment configuration failed. Unsupported type 'class java.lang.Boolean'\\.(?s).*"
      },

      // unsupported format of list environment
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   - MYSQL_ROOT_PASSWORD: root\n",
        "Parsing of environment configuration failed. Unsupported value '\\[\\{MYSQL_ROOT_PASSWORD=root}]'\\.(?s).*"
      },

      // dictionary format, no colon in entry
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   MYSQL_ROOT_PASSWORD",
        "Parsing of environment configuration failed. Unsupported value 'MYSQL_ROOT_PASSWORD'\\.(?s).*"
      },

      // dictionary format, value of variable contains equal sign
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   VAR=val=1",
        "Parsing of environment configuration failed. Unsupported value 'VAR=val=1'\\.(?s).*"
      },

      // array format, no equal sign in entry
      {
        "services: \n"
            + " dev-machine: \n"
            + "  image: codenvy/ubuntu_jdk8\n"
            + "  environment:\n"
            + "   - MYSQL_ROOT_PASSWORD=root\n"
            + "   - MYSQL_DATABASE\n",
        "Parsing of environment configuration failed. Unsupported value 'MYSQL_DATABASE'\\.(?s).*"
      },
    };
  }

  @Test
  public void testSetsRamLimitAttributeFromComposeService() throws Exception {
    final long customRamLimit = 3072;
    final Map<String, InternalMachineConfig> machines =
        ImmutableMap.of(
            MACHINE_NAME_1,
            mockInternalMachineConfig(new HashMap<>()),
            MACHINE_NAME_2,
            mockInternalMachineConfig(new HashMap<>()));
    final Map<String, ComposeService> services =
        ImmutableMap.of(
            MACHINE_NAME_1,
            mockComposeService(customRamLimit),
            MACHINE_NAME_2,
            mockComposeService(customRamLimit));

    factory.setRamLimitAttribute(machines, services);

    final long[] actual =
        machines
            .values()
            .stream()
            .mapToLong(m -> Long.parseLong(m.getAttributes().get(MEMORY_LIMIT_ATTRIBUTE)))
            .toArray();
    final long[] expected = new long[actual.length];
    fill(expected, customRamLimit);
    assertTrue(Arrays.equals(actual, expected));
  }

  @Test
  public void testDoNotOverrideRamLimitAttributeWhenItAlreadyPresent() throws Exception {
    final long customRamLimit = 3072;
    final Map<String, String> attributes =
        ImmutableMap.of(MEMORY_LIMIT_ATTRIBUTE, String.valueOf(customRamLimit));
    final Map<String, InternalMachineConfig> machines =
        ImmutableMap.of(
            MACHINE_NAME_1,
            mockInternalMachineConfig(attributes),
            MACHINE_NAME_2,
            mockInternalMachineConfig(attributes));
    final Map<String, ComposeService> services =
        ImmutableMap.of(
            MACHINE_NAME_1, mockComposeService(0), MACHINE_NAME_2, mockComposeService(0));

    factory.setRamLimitAttribute(machines, services);

    final long[] actual =
        machines
            .values()
            .stream()
            .mapToLong(m -> Long.parseLong(m.getAttributes().get(MEMORY_LIMIT_ATTRIBUTE)))
            .toArray();
    final long[] expected = new long[actual.length];
    fill(expected, customRamLimit);
    assertTrue(Arrays.equals(actual, expected));
  }

  private static InternalMachineConfig mockInternalMachineConfig(Map<String, String> attributes) {
    final InternalMachineConfig machineConfigMock = mock(InternalMachineConfig.class);
    when(machineConfigMock.getAttributes()).thenReturn(attributes);
    return machineConfigMock;
  }

  private static ComposeService mockComposeService(long ramLimit) {
    final ComposeService composeServiceMock = mock(ComposeService.class);
    when(composeServiceMock.getMemLimit()).thenReturn(ramLimit);
    return composeServiceMock;
  }
}
