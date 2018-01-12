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

import {HttpInterceptorBase} from './interceptor-base';

const GITHUB_API = 'api.github.com';

/**
 * @author Oleksii Kurinnyi
 */
export class KeycloakTokenInterceptor extends HttpInterceptorBase {
  $log: ng.ILogService;
  $q: ng.IQService;
  keycloak: any;
  keycloakConfig: any;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($log: ng.ILogService,
              $q: ng.IQService,
              keycloakAuth: any) {
    super();

    this.$log = $log;
    this.$q = $q;
    this.keycloak = keycloakAuth.keycloak;
    this.keycloakConfig = keycloakAuth.config;
  }

  request(config: any): ng.IPromise<any> {
    if (this.keycloak && config.url.indexOf(this.keycloakConfig.url) > -1) {
      return config;
    }

    if (config.url.indexOf(GITHUB_API) > -1) {
      return config;
    }

    if (this.keycloak && this.keycloak.token) {
      let deferred = this.$q.defer();
      this.keycloak.updateToken(5).success(() => {
        config.headers = config.headers || {};
        config.headers.Authorization = 'Bearer ' + this.keycloak.token;
        deferred.resolve(config);
      }).error(() => {
        this.$log.log('token refresh failed :' + config.url);
        deferred.reject('Failed to refresh token');
        this.keycloak.login();
      });
      return deferred.promise;
    }
    return config || this.$q.when(config);
  }

  response(response: any): ng.IPromise<any> {
    return response || this.$q.when(response);
  }

  responseError(rejection: any): ng.IPromise<any> {
    return this.$q.reject(rejection);
  }

  requestError(rejection: any): ng.IPromise<any> {
    return this.$q.reject(rejection);
  }

}
