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
package org.eclipse.che.ide.util.storage;

import javax.annotation.Nonnull;
import org.eclipse.che.commons.annotation.Nullable;

/**
 * Stores client-side data into a storage.
 *
 * @author Anatoliy Bazko
 */
public interface LocalStorage {

  /** Gets value from the storage. Method returns null if value doesn't exist. */
  @Nullable
  String getItem(@Nonnull String key);

  /** Removes value from the storage. */
  void removeItem(@Nonnull String key);

  /** Puts value into the storage. */
  void setItem(@Nonnull String key, @Nonnull String value);

  /** Returns the key at the specified index. */
  String key(int index);

  /** Returns the size of the local storage. */
  int getLength();
}
