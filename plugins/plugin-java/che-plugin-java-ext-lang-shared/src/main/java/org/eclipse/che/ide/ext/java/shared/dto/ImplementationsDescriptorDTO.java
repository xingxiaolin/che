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
import org.eclipse.che.ide.ext.java.shared.dto.model.Type;

/**
 * DTO represents the information about implementing members.
 *
 * @author Valeriy Svydenko
 */
@DTO
public interface ImplementationsDescriptorDTO {

  /** Returns name of implemented member. */
  String getMemberName();

  void setMemberName(String memberName);

  ImplementationsDescriptorDTO withMemberName(String memberName);

  /** Returns all implementations. */
  List<Type> getImplementations();

  void setImplementations(List<Type> implementations);

  ImplementationsDescriptorDTO withImplementations(List<Type> implementations);
}
