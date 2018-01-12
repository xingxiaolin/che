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
 * Defines a directive for checking git URL
 * @author Florent Benoit
 */
export class GitUrlValidator implements ng.IDirective {

  restrict = 'A';
  require = 'ngModel';

  /**
   * Check that the GIT URL is compliant
   */
  link($scope: ng.IScope, $element: ng.IAugmentedJQuery, $attributes: ng.IAttributes, $ngModel: ng.INgModelController): void {
    ($ngModel.$validators as any).gitUrl = function(modelValue: string) {
      var res = /((git|ssh|http(s)?)|(git@[\w\.]+))(:(\/\/))?([\w\.@\:/\-~]+)(\.git)?(\/)?/.test(modelValue);
      return res;
    };
  }

}
