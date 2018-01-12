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
 * This class is fetch and handling the user permission data for organizations
 *
 * @author Oleksii Orel
 */
export class OrganizationsPermissionService {

  /**
   * Permissions API interaction.
   */
  private chePermissions: che.api.IChePermissions;
  /**
   * User API interaction.
   */
  private cheUser: any;
  /**
   * User id.
   */
  private userId: string;

  private fetchingMap: Map<string, ng.IPromise<any>> = new Map();

  /**
   * @ngInject for Dependency injection
   */
  constructor(chePermissions: che.api.IChePermissions, cheUser: any) {
    this.chePermissions = chePermissions;
    this.cheUser = cheUser;

    let user = this.cheUser.getUser();
    if (user) {
      this.userId = user.id;
    } else {
      this.cheUser.fetchUser().then((user: che.IUser) => {
        this.userId = user.id;
      });
    }
  }

  fetchPermissions(organizationId: string): ng.IPromise<any> {
    if (this.fetchingMap.get(organizationId)) {
      return this.fetchingMap.get(organizationId);
    }
    let promise = this.chePermissions.fetchOrganizationPermissions(organizationId);
    this.fetchingMap.set(organizationId, promise);
    promise.finally(() => {
      this.fetchingMap.delete(organizationId);
    });
  }

  /**
   * Checks whether user is allowed to perform pointed action.
   *
   * @param action {string} action
   * @param organizationId {string} organization id
   * @returns {boolean} <code>true</code> if allowed
   */
  isUserAllowedTo(action: string, organizationId: string): boolean {
    if (!organizationId || !action) {
      return false;
    }
    let permissions = this.chePermissions.getOrganizationPermissions(organizationId);
    if (!permissions) {
      this.fetchPermissions(organizationId);
      return false;
    }
    return !angular.isUndefined(permissions.find((permission: che.IPermissions) => {
      return permission.userId === this.userId && permission.actions.indexOf(action.toString()) !== -1;
    }));
  }
}
