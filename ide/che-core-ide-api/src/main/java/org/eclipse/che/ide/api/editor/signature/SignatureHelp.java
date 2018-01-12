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
 * Result of calculation of signatures, represent the signature of something callable.
 *
 * @author Evgen Vidolob
 */
public interface SignatureHelp {

  /**
   * One or more signature.
   *
   * @return
   */
  @NotNull
  List<SignatureInfo> getSignatures();

  /**
   * The active signature
   *
   * @return
   */
  Optional<Integer> getActiveSignature();

  /**
   * The active parameter of the active signature.
   *
   * @return
   */
  Optional<Integer> getActiveParameter();
}
