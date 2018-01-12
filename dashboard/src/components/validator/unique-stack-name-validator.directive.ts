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
import {CheAPI} from '../api/che-api.factory';

interface IUniqueStackNameValidatorAttributes extends ng.IAttributes {
  uniqueStackName: string;
}

/**
 * Defines a directive for checking whether stack name already exists.
 *
 * @author Ann Shumilova
 */
export class UniqueStackNameValidator implements ng.IDirective {
  restrict = 'A';
  require = 'ngModel';

  cheAPI: CheAPI;
  $q: ng.IQService;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor (cheAPI: CheAPI, $q: ng.IQService) {
    this.cheAPI = cheAPI;
    this.$q = $q;
  }

  /**
   * Check that the name of stack is unique
   */
  link($scope: ng.IScope, $element: ng.IAugmentedJQuery, $attributes: ng.IAttributes, $ngModelCtrl: ng.INgModelController) {

    // validate only input element
    if ('input' === $element[0].localName) {

      ($ngModelCtrl.$asyncValidators as any).uniqueStackName = (modelValue: string) => {

        // create promise
        const deferred = this.$q.defer();

        // parent scope ?
        let scopingTest = $scope.$parent;
        if (!scopingTest) {
          scopingTest = $scope;
        }

        let currentStackName = scopingTest.$eval(($attributes as any).uniqueStackName),
          stacks = this.cheAPI.getStack().getStacks();
        if (stacks.length) {
          for (let i = 0; i < stacks.length; i++) {
            if (stacks[i].name === currentStackName) {
              continue;
            }
            if (stacks[i].name === modelValue) {
              deferred.reject(false);
            }
          }
          deferred.resolve(true);
        } else {
          deferred.resolve(true);
        }
        return deferred.promise;
      };
    }
  }
}
