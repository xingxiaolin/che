package org.eclipse.che.workspace.infrastructure.openshift.util;

import static org.testng.Assert.assertEquals;

import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests {@link KubernetesSize}.
 *
 * @author Anton Korneta
 */
@Listeners(MockitoTestNGListener.class)
public class KubernetesSizeTest {

  @Test(dataProvider = "validSizes")
  public void testParseKubernetesMemoryFormatsToBytes(String kubeSize, long expectedBytes)
      throws Exception {
    assertEquals(KubernetesSize.toBytes(kubeSize), expectedBytes);
  }

  @DataProvider(name = "validSizes")
  public Object[][] correctKubernetesMemoryFormats() {
    return new Object[][] {
      {"123Mi", 128974848}, {"129M", 129000000}, {"129e6", 129000000}, {"129e+6", 129000000}
    };
  }
}
