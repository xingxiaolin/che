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
package org.eclipse.che.api.debug.shared.dto.action;

import java.util.List;
import org.eclipse.che.api.debug.shared.dto.BreakpointDto;
import org.eclipse.che.api.debug.shared.model.action.StartAction;
import org.eclipse.che.dto.shared.DTO;

/** @author Anatoliy Bazko */
@DTO
public interface StartActionDto extends ActionDto, StartAction {
  TYPE getType();

  void setType(TYPE type);

  StartActionDto withType(TYPE type);

  List<BreakpointDto> getBreakpoints();

  void setBreakpoints(List<BreakpointDto> breakpoints);

  StartActionDto withBreakpoints(List<BreakpointDto> breakpoints);
}
