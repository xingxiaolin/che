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
package org.eclipse.che.plugin.typescript.dto;

import org.eclipse.che.dto.shared.DTO;

/** @author Florent Benoit */
@DTO
public interface MyOtherDTO extends MySuperClassDTO, MySuperSuperClass {

  void setName(String name);

  MyOtherDTO withName(String name);
}
