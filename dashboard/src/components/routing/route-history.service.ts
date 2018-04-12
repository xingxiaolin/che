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
 * Provides a way to know the history of all routes that have been loaded
 * @author Florent Benoit
 */
export class RouteHistory {

  static $inject = ['$rootScope', '$location'];

  history: string[];

  /**
   * Default constructor that is using resource injection
   */
  constructor($rootScope: ng.IRootScopeService,
              $location: ng.ILocationService) {
    this.history = [];
    $rootScope.$on('$routeChangeSuccess', () => {
      this.history.push($location.path());
    });
  }

  /**
   * Add a new path on top of all existing paths
   * @param {string} path the path on which we will we redirecting when we pop current path
   */
  pushPath(path: string): void {
    this.history.push(path);
  }

  getPaths() {
    return this.history;
  }

  getLastPath() {
    if (this.history.length === 0) {
      return '';
    }
    return this.history[this.history.length - 1];
  }

  /**
   * Pop current path from the history.
   */
  popCurrentPath() {
    this.history.pop();
  }

}
