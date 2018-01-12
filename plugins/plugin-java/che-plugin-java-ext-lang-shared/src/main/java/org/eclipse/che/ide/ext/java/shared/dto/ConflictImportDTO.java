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
package org.eclipse.che.ide.ext.java.shared.dto;

import java.util.List;
import org.eclipse.che.dto.shared.DTO;

/**
 * DTO represents the information about the imports for the organize imports process.
 *
 * @author Valeriy Svydenko
 */
@DTO
public interface ConflictImportDTO {
  /** Returns list of the possible imports for the current conflict. */
  List<String> getTypeMatches();

  void setTypeMatches(List<String> typeMatches);

  ConflictImportDTO withTypeMatches(List<String> typeMatches);
}
