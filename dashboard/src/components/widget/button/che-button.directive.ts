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
 * Defines the super class for for all buttons
 * @author Florent Benoit
 */
export abstract class CheButton {
  restrict: string = 'E';
  bindToController: boolean = true;

  /**
   * Template for the current toolbar
   * @param element
   * @param attrs
   * @returns {string} the template
   */
  template(element: ng.IAugmentedJQuery, attrs: any) {
    let template: string = this.getTemplateStart();

    if (attrs.href) {
      template = template + ` href="${attrs.href}"`;
    }

    if (attrs.target) {
      template = template + ` target="${attrs.target}"`;
    }

    if (attrs.ngClick) {
      template = template + ` ng-click="${attrs.ngClick}"`;
    }

    if (attrs.ngHref) {
      template = template + ` ng-href="${attrs.ngHref}"`;
    }

    if (attrs.ngDisabled) {
      template = template + ` disabled="${attrs.ngHref}"`;
    }

    template = template + '>';

    if (attrs.cheButtonIcon) {
      template = template + `<md-icon md-font-icon="${attrs.cheButtonIcon}" flex layout="column" layout-align="start center"></md-icon>`;
    }


    template = template + attrs.cheButtonTitle + '</md-button>';
    return template;
  }

  abstract getTemplateStart(): string;

  compile(element: ng.IAugmentedJQuery, attrs: any) {
    let button = element.find('button');
    if (attrs && attrs.tabindex) {
      button.attr('tabindex', attrs.tabindex);
    } else {
      button.attr('tabindex', 0);
    }
    // top level element doesn't have tabindex, only the button has
    element.attr('tabindex', -1);

    attrs.$set('ngClick', undefined);
  }

  /**
   * Re-apply ng-disabled on child
   */
  link($scope: ng.IScope, element: ng.IAugmentedJQuery, attrs: any) {
    $scope.$watch(attrs.ngDisabled, function (isDisabled: boolean) {
      element.find('button').prop('disabled', isDisabled);
    });

  }

}
