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
package org.eclipse.che.ide.api.resources.modification;

import com.google.common.annotations.Beta;

/**
 * Manages providers that maintain a clipboard. Provides are responsible for cut/copy/paste
 * operations.
 *
 * @author Vlad Zhukovskiy
 * @see CutProvider
 * @see CopyProvider
 * @see PasteProvider
 * @since 4.4.0
 */
@Beta
public interface ClipboardManager {
  /**
   * Returns the cut operation provider.
   *
   * @return the cut provider
   * @see CutProvider
   * @since 4.4.0
   */
  CutProvider getCutProvider();

  /**
   * Returns the copy operation provider.
   *
   * @return the copy provider
   * @see CopyProvider
   * @since 4.4.0
   */
  CopyProvider getCopyProvider();

  /**
   * Returns the paste operation provider.
   *
   * @return the paste provider
   * @see PasteProvider
   * @since 4.4.0
   */
  PasteProvider getPasteProvider();
}
