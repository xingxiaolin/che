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
package org.eclipse.che.workspace.infrastructure.kubernetes.namespace;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import io.fabric8.kubernetes.api.model.DoneableNamespace;
import io.fabric8.kubernetes.api.model.DoneableServiceAccount;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceFluent.MetadataNested;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.workspace.infrastructure.kubernetes.KubernetesClientFactory;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests {@link KubernetesNamespace}
 *
 * @author Sergii Leshchenko
 */
@Listeners(MockitoTestNGListener.class)
public class KubernetesNamespaceTest {

  public static final String NAMESPACE = "testNamespace";
  public static final String WORKSPACE_ID = "workspace123";

  @Mock private KubernetesPods pods;
  @Mock private KubernetesServices services;
  @Mock private KubernetesIngresses ingresses;
  @Mock private KubernetesPersistentVolumeClaims pvcs;
  @Mock private KubernetesClientFactory clientFactory;
  @Mock private KubernetesClient kubernetesClient;
  @Mock private NonNamespaceOperation namespaceOperation;
  @Mock private Resource<ServiceAccount, DoneableServiceAccount> serviceAccountResource;

  private KubernetesNamespace k8sNamespace;

  @BeforeMethod
  public void setUp() throws Exception {
    when(clientFactory.create()).thenReturn(kubernetesClient);
    when(clientFactory.create(anyString())).thenReturn(kubernetesClient);

    doReturn(namespaceOperation).when(kubernetesClient).namespaces();

    final MixedOperation mixedOperation = mock(MixedOperation.class);
    final NonNamespaceOperation namespaceOperation = mock(NonNamespaceOperation.class);
    doReturn(mixedOperation).when(kubernetesClient).serviceAccounts();
    when(mixedOperation.inNamespace(anyString())).thenReturn(namespaceOperation);
    when(namespaceOperation.withName(anyString())).thenReturn(serviceAccountResource);
    when(serviceAccountResource.get()).thenReturn(mock(ServiceAccount.class));

    k8sNamespace = new KubernetesNamespace(WORKSPACE_ID, pods, services, pvcs, ingresses);
  }

  @Test
  public void testKubernetesNamespaceCreationWhenNamespaceExists() throws Exception {
    // given
    prepareNamespace(NAMESPACE);

    // when
    new KubernetesNamespace(clientFactory, NAMESPACE, WORKSPACE_ID);
  }

  @Test
  public void testKubernetesNamespaceCreationWhenNamespaceDoesNotExist() throws Exception {
    // given
    MetadataNested namespaceMeta = prepareCreateNamespaceRequest();

    Resource resource = prepareNamespaceResource(NAMESPACE);
    doThrow(new KubernetesClientException("error", 403, null)).when(resource).get();

    // when
    KubernetesNamespace namespace = new KubernetesNamespace(clientFactory, NAMESPACE, WORKSPACE_ID);

    // then
    verify(namespaceMeta).withName(NAMESPACE);
  }

  @Test
  public void testKubernetesNamespaceCleaningUp() throws Exception {
    // when
    k8sNamespace.cleanUp();

    verify(ingresses).delete();
    verify(services).delete();
    verify(pods).delete();
  }

  @Test
  public void testKubernetesNamespaceCleaningUpIfExceptionsOccurs() throws Exception {
    doThrow(new InfrastructureException("err1.")).when(services).delete();
    doThrow(new InfrastructureException("err2.")).when(pods).delete();

    InfrastructureException error = null;
    // when
    try {
      k8sNamespace.cleanUp();

    } catch (InfrastructureException e) {
      error = e;
    }

    // then
    assertNotNull(error);
    String message = error.getMessage();
    assertEquals(message, "Error(s) occurs while cleaning up the namespace. err1. err2.");
    verify(ingresses).delete();
  }

  @Test(expectedExceptions = InfrastructureException.class)
  public void testThrowsInfrastructureExceptionWhenFailedToGetNamespaceServiceAccounts()
      throws Exception {
    prepareCreateNamespaceRequest();
    final Resource resource = prepareNamespaceResource(NAMESPACE);
    doThrow(new KubernetesClientException("error", 403, null)).when(resource).get();
    doThrow(KubernetesClientException.class).when(kubernetesClient).serviceAccounts();

    new KubernetesNamespace(clientFactory, NAMESPACE, WORKSPACE_ID);
  }

  @Test(expectedExceptions = InfrastructureException.class)
  public void testThrowsInfrastructureExceptionWhenServiceAccountEventNotPublished()
      throws Exception {
    prepareCreateNamespaceRequest();
    final Resource resource = prepareNamespaceResource(NAMESPACE);
    doThrow(new KubernetesClientException("error", 403, null)).when(resource).get();
    when(serviceAccountResource.get()).thenReturn(null);

    new KubernetesNamespace(clientFactory, NAMESPACE, WORKSPACE_ID);
  }

  @Test(expectedExceptions = InfrastructureException.class)
  public void testThrowsInfrastructureExceptionWhenWatcherClosed() throws Exception {
    prepareCreateNamespaceRequest();
    final Resource resource = prepareNamespaceResource(NAMESPACE);
    doThrow(new KubernetesClientException("error", 403, null)).when(resource).get();
    when(serviceAccountResource.get()).thenReturn(null);
    doAnswer(
            (Answer<Watch>)
                invocation -> {
                  final Watcher<ServiceAccount> watcher = invocation.getArgument(0);
                  watcher.onClose(mock(KubernetesClientException.class));
                  return mock(Watch.class);
                })
        .when(serviceAccountResource)
        .watch(any());

    new KubernetesNamespace(clientFactory, NAMESPACE, WORKSPACE_ID);
  }

  @Test
  public void testStopsWaitingServiceAccountEventJustAfterEventReceived() throws Exception {
    prepareCreateNamespaceRequest();
    final Resource resource = prepareNamespaceResource(NAMESPACE);
    doThrow(new KubernetesClientException("error", 403, null)).when(resource).get();
    when(serviceAccountResource.get()).thenReturn(null);
    doAnswer(
            (Answer<Watch>)
                invocation -> {
                  final Watcher<ServiceAccount> watcher = invocation.getArgument(0);
                  watcher.eventReceived(Action.ADDED, mock(ServiceAccount.class));
                  return mock(Watch.class);
                })
        .when(serviceAccountResource)
        .watch(any());

    new KubernetesNamespace(clientFactory, NAMESPACE, WORKSPACE_ID);

    verify(serviceAccountResource).get();
    verify(serviceAccountResource).watch(any());
  }

  private MetadataNested prepareCreateNamespaceRequest() {
    DoneableNamespace namespace = mock(DoneableNamespace.class);
    MetadataNested metadataNested = mock(MetadataNested.class);

    doReturn(namespace).when(namespaceOperation).createNew();
    doReturn(metadataNested).when(namespace).withNewMetadata();
    doReturn(metadataNested).when(metadataNested).withName(anyString());
    doReturn(namespace).when(metadataNested).endMetadata();
    return metadataNested;
  }

  private Resource prepareNamespaceResource(String namespaceName) {
    Resource namespaceResource = mock(Resource.class);
    doReturn(namespaceResource).when(namespaceOperation).withName(namespaceName);
    kubernetesClient.namespaces().withName(namespaceName).get();
    return namespaceResource;
  }

  private void prepareNamespace(String namespaceName) {
    Namespace namespace = mock(Namespace.class);
    Resource namespaceResource = prepareNamespaceResource(namespaceName);
    doReturn(namespace).when(namespaceResource).get();
  }
}
