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
package org.eclipse.che.ide.ui.multisplitpanel.tab;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ui.multisplitpanel.SubPanel;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Interface for a tab associated with some widget on the {@link SubPanel}.
 *
 * @author Artem Zatsarynnyi
 */
public interface Tab extends View<Tab.ActionDelegate>, ClickHandler, DoubleClickHandler {

  /** Returns the icon associated with tab. */
  SVGResource getIcon();

  /** Returns the title text for the tab. */
  String getTitleText();

  void select();

  void unSelect();

  interface ActionDelegate {

    /** Called when {@code tab} has been clicked. */
    void onTabClicked(Tab tab);

    /** Called when {@code tab} has been double clicked. */
    void onTabDoubleClicked(Tab tab);

    /** Called when {@code tab} is going to be closed. */
    void onTabClosing(Tab tab);
  }
}
