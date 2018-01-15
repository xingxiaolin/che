package org.eclipse.che.workspace.infrastructure.docker.environment.compose;

import static java.util.Arrays.fill;
import static org.eclipse.che.api.core.model.workspace.config.MachineConfig.MEMORY_LIMIT_ATTRIBUTE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.api.installer.server.InstallerRegistry;
import org.eclipse.che.api.workspace.server.spi.environment.InternalMachineConfig;
import org.eclipse.che.api.workspace.server.spi.environment.MachineConfigsValidator;
import org.eclipse.che.api.workspace.server.spi.environment.RecipeRetriever;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.model.ComposeService;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests {@link ComposeEnvironmentFactory}.
 *
 * @author Anton Korneta
 */
@Listeners(MockitoTestNGListener.class)
public class ComposeEnvironmentFactoryTest {

  private static final String MACHINE_NAME_1 = "machine1";
  private static final String MACHINE_NAME_2 = "machine2";

  @Mock private InstallerRegistry installerRegistry;
  @Mock private RecipeRetriever recipeRetriever;
  @Mock private MachineConfigsValidator machinesValidator;
  @Mock private ComposeEnvironmentValidator composeValidator;
  @Mock private ComposeServicesStartStrategy startStrategy;

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
            2048);
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
