/*
 * Copyright (c) 2015-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
'use strict';

/**
 * This class is providing a builder for Project Reference
 * @author Florent Benoit
 */
export class CheProjectReferenceBuilder {
  private projectReference: any;

  /**
   * Default constructor.
   */
  constructor() {
    this.projectReference = {};
  }

  /**
   * Sets the name of the project reference
   * @param {string} name the name to use
   * @returns {CheProjectReferenceBuilder}
   */
  withName(name: string): CheProjectReferenceBuilder {
    this.projectReference.name = name;
    return this;
  }

  /**
   * Build the project reference
   * @returns {CheProjectReferenceBuilder.projectReference|*}
   */
  build(): CheProjectReferenceBuilder {
    return this.projectReference;
  }

}

