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
package org.eclipse.che.plugin.testing.ide.view;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.plugin.testing.ide.model.TestRootState;
import org.eclipse.che.plugin.testing.ide.model.TestStateEventsListener;

/**
 * View for the result of java tests.
 *
 * @author Mirage Abeysekara
 */
public interface TestResultView
    extends View<TestResultView.ActionDelegate>, TestStateEventsListener {
  /**
   * Sets whether this panel is visible.
   *
   * @param visible visible - true to show the object, false to hide it
   */
  void setVisible(boolean visible);

  TestRootState getRootState();

  interface ActionDelegate extends BaseActionDelegate {}
}
