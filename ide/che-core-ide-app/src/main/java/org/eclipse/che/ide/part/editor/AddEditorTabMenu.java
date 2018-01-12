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
package org.eclipse.che.ide.part.editor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionGroup;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.keybinding.KeyBindingAgent;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.menu.ContextMenu;

/** Menu appeared after clicking on Add editor tab button. */
public class AddEditorTabMenu extends ContextMenu {

  @Inject
  public AddEditorTabMenu(
      ActionManager actionManager,
      KeyBindingAgent keyBindingAgent,
      Provider<PerspectiveManager> managerProvider) {
    super(actionManager, keyBindingAgent, managerProvider);
  }

  protected ActionGroup updateActions() {
    DefaultActionGroup defaultGroup = new DefaultActionGroup(actionManager);

    final ActionGroup actionGroup =
        (ActionGroup) actionManager.getAction(IdeActions.GROUP_FILE_NEW);

    for (Action action : actionGroup.getChildren(null)) {
      defaultGroup.add(action);
    }

    return defaultGroup;
  }
}
