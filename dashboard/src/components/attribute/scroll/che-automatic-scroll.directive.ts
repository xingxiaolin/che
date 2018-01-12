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

interface ICheAutoScrollAttributes extends ng.IAttributes {
  ngModel: any;
}

/**
 * @ngdoc directive
 * @name components.directive:cheAutoScroll
 * @restrict A
 * @function
 * @element
 *
 * @description
 * `che-auto-scroll` defines an attribute for auto scrolling to the bottom of the element applied.
 *
 * @usage
 *   <text-area che-auto-scroll></text-area>
 *
 * @author Florent Benoit
 */
export class CheAutoScroll {
  restrict = 'A';

  $timeout: ng.ITimeoutService;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($timeout: ng.ITimeoutService) {
    this.$timeout = $timeout;
  }

  /**
   * Keep reference to the model controller
   */
  link($scope: ng.IScope, $element: ng.IAugmentedJQuery, $attrs: ICheAutoScrollAttributes) {
    $scope.$watch($attrs.ngModel, () => {
      this.$timeout(() => {
        $element[0].scrollTop = $element[0].scrollHeight;
      });
    }, true);
  }

}
