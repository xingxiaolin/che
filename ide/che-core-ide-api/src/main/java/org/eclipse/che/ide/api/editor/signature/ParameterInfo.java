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
package org.eclipse.che.ide.api.editor.signature;

import com.google.common.base.Optional;
import javax.validation.constraints.NotNull;

/**
 * Represent a parameter of callable signature. Parameter can have label and optional documentation.
 *
 * @author Evgen Vidolob
 */
public interface ParameterInfo {

  /**
   * The label of this parameter. Used for UI.
   *
   * @return the parameter label.
   */
  @NotNull
  String getLabel();

  /**
   * The documentation of this parameter.
   *
   * @return the human-readable documentation string.
   */
  Optional<String> getDocumentation();
}
