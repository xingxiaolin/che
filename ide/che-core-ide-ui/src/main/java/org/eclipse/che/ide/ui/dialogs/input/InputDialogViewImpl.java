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
package org.eclipse.che.ide.ui.dialogs.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.ui.window.Window;

/**
 * Implementation of the input dialog view.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyi
 */
public class InputDialogViewImpl extends Window implements InputDialogView {

  /** The UI binder instance. */
  private static ConfirmWindowUiBinder uiBinder = GWT.create(ConfirmWindowUiBinder.class);
  /** The window footer. */
  private final InputDialogFooter footer;

  @UiField Label label;
  @UiField TextBox value;
  @UiField Label errorHint;

  private ActionDelegate delegate;
  private int selectionStartIndex;
  private int selectionLength;

  @Inject
  public InputDialogViewImpl(final @NotNull InputDialogFooter footer) {
    Widget widget = uiBinder.createAndBindUi(this);
    setWidget(widget);

    this.footer = footer;
    getFooter().add(this.footer);

    this.ensureDebugId("askValueDialog-window");
    this.value.ensureDebugId("askValueDialog-textBox");
  }

  @Override
  public void show() {
    super.show();
    value.setSelectionRange(selectionStartIndex, selectionLength);
    new Timer() {
      @Override
      public void run() {
        value.setFocus(true);
      }
    }.schedule(300);
  }

  @Override
  public void setDelegate(final ActionDelegate delegate) {
    this.delegate = delegate;
    this.footer.setDelegate(this.delegate);
  }

  @Override
  protected void onEnterClicked() {
    delegate.onEnterClicked();
  }

  @Override
  public void showDialog() {
    this.show();
  }

  @Override
  public void closeDialog() {
    this.hide();
  }

  @Override
  public void setContent(final String label) {
    this.label.setText(label);
  }

  @Override
  public void setValue(String value) {
    this.value.setText(value);
  }

  @Override
  public void setOkButtonLabel(String label) {
    footer.getOkButton().setText(label);
  }

  @Override
  public String getValue() {
    return value.getValue();
  }

  @Override
  public void setSelectionStartIndex(int selectionStartIndex) {
    this.selectionStartIndex = selectionStartIndex;
  }

  @Override
  public void setSelectionLength(int selectionLength) {
    this.selectionLength = selectionLength;
  }

  @Override
  public void showErrorHint(String text) {
    errorHint.setText(text);
    footer.getOkButton().setEnabled(false);
  }

  @Override
  public void hideErrorHint() {
    errorHint.setText("");
    footer.getOkButton().setEnabled(true);
  }

  @Override
  public boolean isOkButtonInFocus() {
    return isWidgetFocused(footer.okButton);
  }

  @Override
  public boolean isCancelButtonInFocus() {
    return isWidgetFocused(footer.cancelButton);
  }

  @UiHandler("value")
  void onKeyUp(KeyUpEvent event) {
    delegate.inputValueChanged();
  }

  /** The UI binder interface for this components. */
  interface ConfirmWindowUiBinder extends UiBinder<Widget, InputDialogViewImpl> {}
}
