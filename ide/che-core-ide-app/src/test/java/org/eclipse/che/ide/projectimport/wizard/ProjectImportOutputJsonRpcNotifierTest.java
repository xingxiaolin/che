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
package org.eclipse.che.ide.projectimport.wizard;

import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.function.Consumer;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerManager;
import org.eclipse.che.api.core.jsonrpc.commons.reception.ConsumerConfiguratorOneToNone;
import org.eclipse.che.api.core.jsonrpc.commons.reception.MethodNameConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.reception.ParamsConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.reception.ResultConfiguratorFromOne;
import org.eclipse.che.api.project.shared.dto.ImportProgressRecordDto;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode;
import org.eclipse.che.ide.api.notification.StatusNotification.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

/**
 * Unit tests for {@link ProjectImportOutputJsonRpcNotifier}.
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectImportOutputJsonRpcNotifierTest {

  @Mock NotificationManager notificationManager;
  @Mock CoreLocalizationConstant constant;
  @Mock EventBus eventBus;
  @Mock RequestHandlerConfigurator configurator;
  @Mock RequestHandlerManager manager;

  private ProjectImportOutputJsonRpcNotifier notifier;

  @Before
  public void setUp() throws Exception {
    notifier =
        new ProjectImportOutputJsonRpcNotifier(
            notificationManager, constant, eventBus, configurator, manager);
  }

  @Test
  public void testShouldSubscribeForDisplayingNotification() throws Exception {
    // given
    final ImportProgressRecordDto dto = mock(ImportProgressRecordDto.class);
    when(dto.getNum()).thenReturn(1);
    when(dto.getLine()).thenReturn("message");
    when(dto.getProjectName()).thenReturn("project");

    final ArgumentCaptor<Consumer> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
    final StatusNotification statusNotification = mock(StatusNotification.class);
    when(notificationManager.notify(anyString(), any(Status.class), any(DisplayMode.class)))
        .thenReturn(statusNotification);
    when(constant.importingProject(anyString())).thenReturn("message");
    final MethodNameConfigurator methodNameConfigurator = mock(MethodNameConfigurator.class);
    when(configurator.newConfiguration()).thenReturn(methodNameConfigurator);
    final ParamsConfigurator paramsConfigurator = mock(ParamsConfigurator.class);
    when(methodNameConfigurator.methodName(anyString())).thenReturn(paramsConfigurator);
    final ResultConfiguratorFromOne resultConfiguratorFromOne =
        mock(ResultConfiguratorFromOne.class);
    when(paramsConfigurator.paramsAsDto(any())).thenReturn(resultConfiguratorFromOne);
    final ConsumerConfiguratorOneToNone consumerConfiguratorOneToNone =
        mock(ConsumerConfiguratorOneToNone.class);
    when(resultConfiguratorFromOne.noResult()).thenReturn(consumerConfiguratorOneToNone);

    // when
    notifier.subscribe("project");

    // then
    verify(constant).importingProject(eq("project"));
    verify(consumerConfiguratorOneToNone).withConsumer(argumentCaptor.capture());
    argumentCaptor.getValue().accept(dto);
    verify(statusNotification).setTitle(eq("message"));
    verify(statusNotification).setContent(eq(dto.getLine()));
  }

  @Test
  public void testShouldUnSubscribeFromDisplayingNotification() throws Exception {
    // given
    when(constant.importProjectMessageSuccess(nullable(String.class))).thenReturn("message");
    final StatusNotification statusNotification = mock(StatusNotification.class);
    when(notificationManager.notify(
            nullable(String.class), nullable(Status.class), nullable(DisplayMode.class)))
        .thenReturn(statusNotification);
    final MethodNameConfigurator methodNameConfigurator = mock(MethodNameConfigurator.class);
    when(configurator.newConfiguration()).thenReturn(methodNameConfigurator);
    final ParamsConfigurator paramsConfigurator = mock(ParamsConfigurator.class);
    when(methodNameConfigurator.methodName(nullable(String.class))).thenReturn(paramsConfigurator);
    final ResultConfiguratorFromOne resultConfiguratorFromOne =
        mock(ResultConfiguratorFromOne.class);
    when(paramsConfigurator.paramsAsDto(any())).thenReturn(resultConfiguratorFromOne);
    final ConsumerConfiguratorOneToNone consumerConfiguratorOneToNone =
        mock(ConsumerConfiguratorOneToNone.class);
    when(resultConfiguratorFromOne.noResult()).thenReturn(consumerConfiguratorOneToNone);

    // when
    notifier.subscribe("project");
    notifier.onSuccess();

    // then
    verify(statusNotification).setStatus(eq(SUCCESS));
    verify(statusNotification).setTitle(eq("message"));
    verify(statusNotification).setContent(eq(""));
  }

  @Test
  public void testShouldUnSubscribeFromDisplayingNotificationIfExceptionOccurred()
      throws Exception {

    // given
    final StatusNotification statusNotification = mock(StatusNotification.class);
    when(notificationManager.notify(
            nullable(String.class), nullable(Status.class), nullable(DisplayMode.class)))
        .thenReturn(statusNotification);
    final MethodNameConfigurator methodNameConfigurator = mock(MethodNameConfigurator.class);
    when(configurator.newConfiguration()).thenReturn(methodNameConfigurator);
    final ParamsConfigurator paramsConfigurator = mock(ParamsConfigurator.class);
    when(methodNameConfigurator.methodName(nullable(String.class))).thenReturn(paramsConfigurator);
    final ResultConfiguratorFromOne resultConfiguratorFromOne =
        mock(ResultConfiguratorFromOne.class);
    when(paramsConfigurator.paramsAsDto(any())).thenReturn(resultConfiguratorFromOne);
    final ConsumerConfiguratorOneToNone consumerConfiguratorOneToNone =
        mock(ConsumerConfiguratorOneToNone.class);
    when(resultConfiguratorFromOne.noResult()).thenReturn(consumerConfiguratorOneToNone);

    // when
    notifier.subscribe("project");
    notifier.onFailure("message");

    // then
    verify(statusNotification).setStatus(eq(FAIL));
    verify(statusNotification).setContent(eq("message"));
  }
}
