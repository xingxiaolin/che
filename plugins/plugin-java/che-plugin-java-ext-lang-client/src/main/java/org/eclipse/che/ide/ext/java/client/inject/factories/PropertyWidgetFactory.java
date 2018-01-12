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
package org.eclipse.che.ide.ext.java.client.inject.factories;

import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget;

/**
 * The factory which creates instances of {@link PropertyWidget}.
 *
 * @author Dmitry Shnurenko
 */
public interface PropertyWidgetFactory {

  /**
   * Creates new instances of {@link PropertyWidget}. Each call of method returns new instance of
   * widget.
   *
   * @param optionId property id which need set to property. Each property has unique id which we
   *     get from server.
   * @return an instance of {@link PropertyWidget}
   */
  PropertyWidget create(@NotNull ErrorWarningsOptions optionId);
}
