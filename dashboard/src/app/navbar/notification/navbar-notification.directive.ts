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
 * @ngDoc directive
 * @name navbar.directive:NavbarDropdownMenu
 * @description This class is handling the directive to handle the container with notifications
 * @author Ann Shumilova
 */
export class NavbarNotification {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor() {
    this.restrict = 'E';
    this.bindToController = true;
    this.templateUrl = 'app/navbar/notification/navbar-notification.html';
    this.controller = 'NavbarNotificationController';
    this.controllerAs = 'navbarNotificationController';

    this.transclude = true;

  }

}
