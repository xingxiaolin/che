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
package org.eclipse.che.api.core.model.project.type;

/**
 * Model interface for Project type's attribute
 *
 * @author gazarenkov
 */
public interface Attribute {

  /** @return attribute unique Id */
  String getId();

  /** @return attribute name */
  String getName();

  /** @return project type this attribute belongs to */
  String getProjectType();

  /** @return value for this attribute */
  Value getValue();

  /** @return some test description of this attribute */
  String getDescription();

  /** @return true if the attribute is mandatory */
  boolean isRequired();

  /*
   * @return true if attribute value can be changed
   */
  boolean isVariable();
}
