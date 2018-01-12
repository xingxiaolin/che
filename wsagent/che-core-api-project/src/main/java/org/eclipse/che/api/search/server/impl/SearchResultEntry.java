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
package org.eclipse.che.api.search.server.impl;

import java.util.List;
import org.eclipse.che.api.search.server.impl.LuceneSearcher.OffsetData;

/** Single item in {@code SearchResult}. */
public class SearchResultEntry {
  private final String filePath;

  private final List<OffsetData> data;

  public SearchResultEntry(String filePath, List<OffsetData> data) {
    this.filePath = filePath;
    this.data = data;
  }

  public List<OffsetData> getData() {
    return data;
  }

  /** Path of file that matches the search criteria. */
  public String getFilePath() {
    return filePath;
  }
}
