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
package org.eclipse.che.plugin.languageserver.ide.window.dialog;

import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.eclipse.lsp4j.MessageActionItem;

/** Implementation of the message dialog UI */
public class MessageDialogPresenter implements MessageDialogView.ActionDelegate {

  /** This component view. */
  private final MessageDialogView view;

  private Consumer<MessageActionItem> callback;

  @Inject
  public MessageDialogPresenter(final @NotNull MessageDialogView view) {
    this.view = view;

    this.view.setDelegate(this);
  }

  public void show(
      String content,
      String title,
      List<MessageActionItem> actions,
      Consumer<MessageActionItem> callback) {
    this.callback = callback;
    view.setContent(content);
    view.setTitleCaption(title);
    view.setActions(actions);
    view.showDialog();
  }

  @Override
  public void onAction(MessageActionItem actionItem) {
    view.closeDialog();
    callback.accept(actionItem);
  }

  @Override
  public void onEnterClicked() {
    // ignore
  }
}
