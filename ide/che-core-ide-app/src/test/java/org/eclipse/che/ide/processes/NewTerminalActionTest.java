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
package org.eclipse.che.ide.processes;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.machine.MachineResources;
import org.eclipse.che.ide.processes.panel.ProcessesPanelPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/** @author Roman Nikitenko */
@RunWith(GwtMockitoTestRunner.class)
public class NewTerminalActionTest {

  @Mock private ProcessesPanelPresenter processesPanelPresenter;
  @Mock private CoreLocalizationConstant locale;
  @Mock private MachineResources resources;
  @Mock private EventBus eventBus;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private ActionEvent actionEvent;

  @InjectMocks private NewTerminalAction action;

  @Test
  public void constructorShouldBeVerified() {
    verify(locale).newTerminal();
    verify(locale).newTerminalDescription();
  }

  @Test
  public void actionShouldBePerformed() throws Exception {
    action.actionPerformed(actionEvent);

    verify(processesPanelPresenter).newTerminal(any());
  }
}
