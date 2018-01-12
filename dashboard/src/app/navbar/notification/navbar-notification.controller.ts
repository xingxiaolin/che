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
import {ApplicationNotifications} from '../../../components/notification/application-notifications.factory';


/**
 * This class is handling the controller for the notifications container.
 * @author Ann Shumilova
 */
export class NavbarNotificationController {

  private applicationNotifications: ApplicationNotifications;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor(applicationNotifications: ApplicationNotifications, $scope: ng.IScope) {
    this.applicationNotifications = applicationNotifications;
    $scope.$on("$mdMenuClose", () => {
      this.removeReadNotifications();
    });
  }

  /**
   * Returns the number of notifications to be shown to user.
   * @returns {number} number of the notifications
   */
  getNotificationsCount(): number {
    return this.applicationNotifications.getNotifications().length;
  }

  /**
   * Returns the list of notifications.
   *
   * @returns {Array<any>} notifications
   */
  getNotifications(): Array<any> {
    return this.applicationNotifications.getNotifications();
  }

  /**
   * Remove notifications, that are considered read by the user.
   */
  removeReadNotifications(): void {
    let notificationsToRemove = [];
    let notifications = this.applicationNotifications.getNotifications()
    notifications.forEach((notification: any) => {
      if (notification.removeOnRead) {
        notificationsToRemove.push(notification);
      }
    });

    notificationsToRemove.forEach((notification: any) => {
      this.applicationNotifications.removeNotification(notification);
    });
  }
}


