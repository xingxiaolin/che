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
package org.eclipse.che.ide.command.toolbar.processes;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.ide.command.CommandResources;

/**
 * Empty state widget for processes list.
 *
 * @see org.eclipse.che.ide.ui.dropdown.DropdownList#DropdownList(Widget)
 */
@Singleton
class EmptyListWidget extends FlowPanel {

  @Inject
  EmptyListWidget(CommandResources resources) {
    addStyleName(resources.commandToolbarCss().processWidgetText());

    final Label commandNameLabel = new InlineHTML("Ready");
    commandNameLabel.addStyleName(resources.commandToolbarCss().processWidgetCommandNameLabel());

    final Label machineNameLabel = new InlineHTML("&nbsp; - start command");
    machineNameLabel.addStyleName(resources.commandToolbarCss().processWidgetMachineNameLabel());

    add(commandNameLabel);
    add(machineNameLabel);
  }
}
