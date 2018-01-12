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
 * Defines a directive in html element, which value will be self compiled.
 *
 * @author Ann Shumilova
 */
export class CheCompile implements ng.IDirective {

  restrict = 'A';

  $compile: ng.ICompileService;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor ($compile: ng.ICompileService) {
    this.$compile = $compile;
  }

  link($scope: ng.IScope, $element: ng.IAugmentedJQuery, $attrs: ng.IAttributes) {
    $scope.$watch(($attrs as any).cheCompile, (value: string) => {
      $element.html(value);
      this.$compile($element.contents())($scope);
    });

  }
}
