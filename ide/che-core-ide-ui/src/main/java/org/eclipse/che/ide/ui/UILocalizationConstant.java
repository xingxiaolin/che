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
package org.eclipse.che.ide.ui;

import com.google.gwt.i18n.client.Messages;

/**
 * I18n messages interface for the 'CodenvyUI' module.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyi
 */
public interface UILocalizationConstant extends Messages {

  @DefaultMessage("OK")
  String okButtonText();

  @DefaultMessage("Cancel")
  String cancelButtonText();

  @DefaultMessage("Value is not valid")
  String validationErrorMessage();
}
