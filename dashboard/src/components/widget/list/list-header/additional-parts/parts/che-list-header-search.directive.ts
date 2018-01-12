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
 * Defines a directive for creating a "Search" field in list's header.
 *
 * @author Oleksii Kurinnyi
 */
export class CheListHeaderSearch implements ng.IDirective {
  restrict: string = 'E';
  scope: {
    [propName: string]: string;
  } = {
    placeholder: '@',
    query: '=',
    onChange: '&?'
  };

  template(): string {
    return `<div class="che-list-header-search">
              <div flex="100" layout="row">
                <div flex="15"
                     class="che-list-header-search-icon"
                     ng-show="!query">
                  <i class="fa fa-search"></i>
                </div>
                <div flex class="che-list-header-search-input">
                  <input type="text"
                         maxlength="128"
                         placeholder="{{placeholder}}"
                         ng-model="query"
                         ng-change="onChange({'query': query})">
                </div>
                <div flex="10"
                     class="che-list-header-close-icon"
                     ng-click="query=''; onChange({'query': ''})"
                     ng-show="query">
                  <i class="fa fa-close"></i>
                </div>
              </div>
            </div>`;
  }

}
