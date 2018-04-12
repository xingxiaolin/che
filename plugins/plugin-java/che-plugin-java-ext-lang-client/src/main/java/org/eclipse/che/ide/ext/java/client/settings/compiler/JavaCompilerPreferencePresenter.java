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
package org.eclipse.che.ide.ext.java.client.settings.compiler;

import static java.util.Arrays.asList;
import static org.eclipse.che.api.core.model.workspace.WorkspaceStatus.RUNNING;
import static org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode.FLOAT_MODE;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.COMPARING_IDENTICAL_VALUES;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.COMPILER_UNUSED_IMPORT;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.COMPILER_UNUSED_LOCAL;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.DEAD_CODE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.FIELD_HIDES_ANOTHER_VARIABLE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.METHOD_WITH_CONSTRUCTOR_NAME;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.MISSING_DEFAULT_CASE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.MISSING_OVERRIDE_ANNOTATION;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.MISSING_SERIAL_VERSION_UID;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.NO_EFFECT_ASSIGNMENT;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.NULL_POINTER_ACCESS;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.POTENTIAL_NULL_POINTER_ACCESS;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.REDUNDANT_NULL_CHECK;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.TYPE_PARAMETER_HIDE_ANOTHER_TYPE;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.UNCHECKED_TYPE_OPERATION;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.UNNECESSARY_ELSE_STATEMENT;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.UNUSED_PRIVATE_MEMBER;
import static org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions.USAGE_OF_RAW_TYPE;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.preferences.AbstractPreferencePagePresenter;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.api.workspace.event.WorkspaceRunningEvent;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.inject.factories.PropertyWidgetFactory;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget;

/**
 * The class contains business logic which allow control changing of compiler's properties.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class JavaCompilerPreferencePresenter extends AbstractPreferencePagePresenter
    implements PropertyWidget.ActionDelegate, WorkspaceRunningEvent.Handler {
  public static final String CATEGORY = "Java Compiler";

  private final ErrorWarningsView view;
  private final PropertyWidgetFactory propertyFactory;
  private final PreferencesManager preferencesManager;
  private final Provider<NotificationManager> notificationManagerProvider;
  private final JavaLocalizationConstant locale;

  private List<ErrorWarningsOptions> options;
  private Map<String, PropertyWidget> widgets;

  @Inject
  public JavaCompilerPreferencePresenter(
      JavaLocalizationConstant locale,
      ErrorWarningsView view,
      PropertyWidgetFactory propertyFactory,
      @JavaCompilerPreferenceManager PreferencesManager preferencesManager,
      Provider<NotificationManager> notificationManagerProvider) {
    super(locale.compilerSetup(), CATEGORY);

    this.view = view;
    this.propertyFactory = propertyFactory;
    this.preferencesManager = preferencesManager;
    this.notificationManagerProvider = notificationManagerProvider;
    this.locale = locale;
    this.widgets = new HashMap<>();

    fillUpOptions();
  }

  @Inject
  private void initialize(AppContext appContext, EventBus eventBus) {
    eventBus.addHandler(WorkspaceRunningEvent.TYPE, this);

    if (appContext.getWorkspace().getStatus() == RUNNING) {
      updateErrorWarningsPanel();
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDirty() {
    for (PropertyWidget widget : widgets.values()) {
      String propertyName = widget.getOptionId().toString();
      String changedValue = widget.getSelectedValue();

      if (!changedValue.equals(preferencesManager.getValue(propertyName))) {
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void storeChanges() {
    widgets
        .values()
        .forEach(
            widget -> {
              String propertyName = widget.getOptionId().toString();
              String selectedValue = widget.getSelectedValue();

              if (!selectedValue.equals(preferencesManager.getValue(propertyName))) {
                preferencesManager.setValue(propertyName, selectedValue);
              }
            });
  }

  /** {@inheritDoc} */
  @Override
  public void revertChanges() {
    widgets
        .values()
        .forEach(
            widget -> {
              String propertyId = widget.getOptionId().toString();
              String previousValue = preferencesManager.getValue(propertyId);

              if (!widget.getSelectedValue().equals(previousValue)) {
                widget.selectPropertyValue(previousValue);
              }
            });
  }

  /** {@inheritDoc} */
  @Override
  public void onPropertyChanged() {
    delegate.onDirtyChanged();
  }

  /** {@inheritDoc} */
  @Override
  public void go(AcceptsOneWidget container) {
    container.setWidget(view);
  }

  @Override
  public void onWorkspaceRunning(WorkspaceRunningEvent event) {
    updateErrorWarningsPanel();
  }

  private void updateErrorWarningsPanel() {
    preferencesManager
        .loadPreferences()
        .then(
            properties -> {
              options.forEach(this::provideWidget);
            })
        .catchError(
            error -> {
              notificationManagerProvider
                  .get()
                  .notify(
                      locale.unableToLoadJavaCompilerErrorsWarningsSettings(), FAIL, FLOAT_MODE);
            });
  }

  /** Creates a new widget when widget does not exist for given option, updates widget otherwise */
  private void provideWidget(@NotNull ErrorWarningsOptions option) {
    String optionId = option.toString();
    String value = preferencesManager.getValue(optionId);

    if (widgets.containsKey(optionId)) {
      PropertyWidget widget = widgets.get(optionId);
      widget.selectPropertyValue(value);
      return;
    }

    PropertyWidget widget = propertyFactory.create(option);

    widget.selectPropertyValue(value);

    widget.setDelegate(JavaCompilerPreferencePresenter.this);

    widgets.put(optionId, widget);

    view.addProperty(widget);
  }

  private void fillUpOptions() {
    options =
        asList(
            COMPILER_UNUSED_LOCAL,
            COMPILER_UNUSED_IMPORT,
            DEAD_CODE,
            METHOD_WITH_CONSTRUCTOR_NAME,
            UNNECESSARY_ELSE_STATEMENT,
            COMPARING_IDENTICAL_VALUES,
            NO_EFFECT_ASSIGNMENT,
            MISSING_SERIAL_VERSION_UID,
            TYPE_PARAMETER_HIDE_ANOTHER_TYPE,
            FIELD_HIDES_ANOTHER_VARIABLE,
            MISSING_DEFAULT_CASE,
            UNUSED_PRIVATE_MEMBER,
            UNCHECKED_TYPE_OPERATION,
            USAGE_OF_RAW_TYPE,
            MISSING_OVERRIDE_ANNOTATION,
            NULL_POINTER_ACCESS,
            POTENTIAL_NULL_POINTER_ACCESS,
            REDUNDANT_NULL_CHECK);
  }
}
