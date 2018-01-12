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
package org.eclipse.che.ide.projectimport.zip;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.Map;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Testing {@link ZipImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipImporterPagePresenterTest {

  private static final String SKIP_FIRST_LEVEL_PARAM_NAME = "skipFirstLevel";
  private static final String CORRECT_URL = "https://host.com/some/path/angularjs.zip";
  private static final String INCORRECT_URL = " https://host.com/some/path/angularjs.zip";

  @Mock private ZipImporterPageView view;
  @Mock private CoreLocalizationConstant locale;
  @Mock private MutableProjectConfig dataObject;
  @Mock private MutableProjectConfig.MutableSourceStorage sourceStorageDto;
  @Mock private Wizard.UpdateDelegate delegate;
  @Mock private Map<String, String> parameters;
  @InjectMocks private ZipImporterPagePresenter presenter;

  @Before
  public void setUp() {
    when(dataObject.getSource()).thenReturn(sourceStorageDto);
    when(sourceStorageDto.getParameters()).thenReturn(parameters);

    presenter.setUpdateDelegate(delegate);
    presenter.init(dataObject);
  }

  @Test
  public void testGo() {
    AcceptsOneWidget container = mock(AcceptsOneWidget.class);
    when(parameters.get(SKIP_FIRST_LEVEL_PARAM_NAME)).thenReturn("true");

    presenter.go(container);

    verify(container).setWidget(eq(view));
    verify(view).setProjectName(nullable(String.class));
    verify(view).setProjectDescription(nullable(String.class));
    verify(view).setProjectUrl(nullable(String.class));
    verify(view).setSkipFirstLevel(nullable(Boolean.class));
    verify(view).setInputsEnableState(eq(true));
    verify(view).focusInUrlInput();
  }

  @Test
  public void incorrectProjectUrlEnteredTest() {
    when(view.getProjectName()).thenReturn("");
    when(view.getProjectName()).thenReturn("angularjs");

    presenter.projectUrlChanged(INCORRECT_URL);

    verify(view).showUrlError(nullable(String.class));
    verify(delegate).updateControls();
  }

  @Test
  public void projectUrlStartWithWhiteSpaceEnteredTest() {
    when(view.getProjectName()).thenReturn("name");

    presenter.projectUrlChanged(INCORRECT_URL);

    verify(view).showUrlError(eq(locale.importProjectMessageStartWithWhiteSpace()));
    verify(delegate).updateControls();
  }

  @Test
  public void correctProjectUrlEnteredTest() {
    when(view.getProjectName()).thenReturn("", "angularjs");

    presenter.projectUrlChanged(CORRECT_URL);

    verify(view, never()).showUrlError(anyString());
    verify(view).hideNameError();
    verify(view).setProjectName(anyString());
    verify(delegate).updateControls();
  }

  @Test
  public void correctProjectNameEnteredTest() {
    String correctName = "angularjs";
    when(view.getProjectName()).thenReturn(correctName);

    presenter.projectNameChanged(correctName);

    verify(view).hideNameError();
    verify(view, never()).showNameError();
    verify(delegate).updateControls();
  }

  @Test
  public void emptyProjectNameEnteredTest() {
    String emptyName = "";
    when(view.getProjectName()).thenReturn(emptyName);

    presenter.projectNameChanged(emptyName);

    verify(view).showNameError();
    verify(delegate).updateControls();
  }

  @Test
  public void incorrectProjectNameEnteredTest() {
    String incorrectName = "angularjs+";
    when(view.getProjectName()).thenReturn(incorrectName);

    presenter.projectNameChanged(incorrectName);

    verify(view).showNameError();
    verify(delegate).updateControls();
  }

  @Test
  public void skipFirstLevelSelectedTest() {
    presenter.skipFirstLevelChanged(true);

    verify(delegate).updateControls();
  }

  @Test
  public void projectDescriptionChangedTest() {
    String description = "description";
    presenter.projectDescriptionChanged(description);

    verify(delegate).updateControls();
  }

  @Test
  public void pageShouldNotBeReadyIfUrlIsEmpty() throws Exception {
    when(view.getProjectName()).thenReturn("name");

    presenter.projectUrlChanged("");

    verify(view).showUrlError(eq(""));
    verify(delegate).updateControls();
  }
}
