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

import {CheButton} from './che-button.directive';

/**
 * @ngdoc directive
 * @name components.directive:cheButtonDanger
 * @restrict E
 * @function
 * @element
 *
 * @description
 * `<che-button-danger>` defines a danger button.
 *
 * @param {string=} che-button-title the title of the button
 * @param {string=} che-button-icon the optional icon of the button
 *
 * @usage
 *   <che-button-danger che-button-title="hello"></che-button-danger>
 *
 * @example
 * <example module="userDashboard">
 * <file name="index.html">
 * <che-button-danger che-button-title="Hello"></che-button-danger>
 * <che-button-danger che-button-title="Hello" che-button-icon="fa fa-trash"></che-button-danger>
 * </file>
 * </example>
 * @author Florent Benoit
 */
export class CheButtonDanger extends CheButton {

  /**
   * Default constructor that is using resource
   */
  constructor () {
    super();
  }

  /**
   * Template for the buttons
   */
  getTemplateStart(): string {
    return '<md-button md-theme=\"danger\" class=\"che-button md-accent md-raised md-hue-3\"';
  }

}
