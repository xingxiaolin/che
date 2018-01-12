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
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Represents the signature of something callable. A signature can have label, like method name, a
 * documentation and list of parameters
 *
 * @author Evgen Vidolob
 */
public interface SignatureInfo {
  /**
   * The label of this signature.
   *
   * @return
   */
  @NotNull
  String getLabel();

  /**
   * The documentation of this signature
   *
   * @return
   */
  Optional<String> getDocumentation();

  /**
   * The parameters of this signature.
   *
   * @return
   */
  Optional<List<ParameterInfo>> getParameters();
}
