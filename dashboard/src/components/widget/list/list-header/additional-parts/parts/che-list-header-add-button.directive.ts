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
 * Defines a directive for creating an "Add" button in list's header.
 *
 * @author Oleksii Kurinnyi
 */
export class CheListHeaderAddButton implements ng.IDirective {
  restrict: string = 'E';
  scope: {
    [propName: string]: string;
  } = {
    title: '@',
    href: '@?',
    onAdd: '&?'
  };

  template($element: ng.IAugmentedJQuery, $attr: ng.IAttributes): string {
    if (($attr.$attr as any).href) {
      return `<che-button-primary class="che-list-header-add-button"
                                  che-button-title="{{title}}"
                                  ng-href="{{href}}"></che-button-primary>`;
    } else {
      return `<che-button-primary class="che-list-header-add-button"
                                  che-button-title="{{title}}"
                                  ng-click="onAdd()"></che-button-primary>`;
    }
  }

}
