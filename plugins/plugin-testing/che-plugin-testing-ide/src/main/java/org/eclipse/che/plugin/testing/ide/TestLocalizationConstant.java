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
package org.eclipse.che.plugin.testing.ide;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants defined in resource bundle:
 * 'TestLocalizationConstant.properties'.
 *
 * @author Mirage Abeysekara
 */
public interface TestLocalizationConstant extends Messages {

  /* Actions */

  @Key("actionGroup.menu.name")
  String actionGroupMenuName();

  @Key("contextActionGroup.menu.name")
  String contextActionGroupMenuName();

  /* Titles */

  @Key("title.testResultPresenter")
  String titleTestResultPresenter();

  @Key("title.testResultPresenter.toolTip")
  String titleTestResultPresenterToolTip();
}
