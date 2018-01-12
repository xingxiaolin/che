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
package org.eclipse.che.ide.ui.multisplitpanel.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.ui.ElementWidget;

/**
 * Implementation of {@link MenuItem} that represents {@link Action}.
 *
 * @author Artem Zatsarynnyi
 */
public class MenuItemActionWidget extends Composite implements MenuItem<Action> {

  private static final MenuItemActionWidgetUiBinder UI_BINDER =
      GWT.create(MenuItemActionWidgetUiBinder.class);

  @UiField FlowPanel iconPanel;
  @UiField Label title;

  private Action action;
  private ActionDelegate delegate;

  public MenuItemActionWidget(Action action) {
    initWidget(UI_BINDER.createAndBindUi(this));
    this.action = action;

    final Element actionIcon = action.getTemplatePresentation().getImageElement();
    if (actionIcon != null) {
      iconPanel.add(new ElementWidget(actionIcon));
    }

    title.setText(action.getTemplatePresentation().getText());

    addDomHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            if (delegate != null) {
              delegate.onItemSelected(MenuItemActionWidget.this);
            }
          }
        },
        ClickEvent.getType());
  }

  @Override
  public void setDelegate(ActionDelegate delegate) {
    this.delegate = delegate;
  }

  @Override
  public Action getData() {
    return action;
  }

  interface MenuItemActionWidgetUiBinder extends UiBinder<Widget, MenuItemActionWidget> {}
}
