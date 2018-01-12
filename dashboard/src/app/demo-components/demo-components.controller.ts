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

enum Tab {Font, Panel, Selecter, Icons, Buttons, Input, List, Label_container, Stack_selector, Popover};

/**
 * This class is handling the controller for the demo of components
 * @author Florent Benoit
 */
export class DemoComponentsController {

  $location: ng.ILocationService;
  selectedIndex: number;
  tab: Object = Tab;

  booksByAuthor: {
    [author: string]: Array<{title: string}>
  };
  button2Disabled: boolean;
  listItemsDocs: string[];
  listItemsTasks: Array<{
    name: string,
    done: boolean
  }>;

  // number spinner
  number: number;
  numberIsChanged: number;

  placement: any;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($location: ng.ILocationService) {
    this.$location = $location;

    const tab = $location.search().tab;
    if (Tab[tab]) {
      this.selectedIndex = parseInt(Tab[tab], 10);
    } else {
      this.selectedIndex = Tab.Font;
    }
    this.placement = {
      options: [
        'top',
        'top-left',
        'top-right',
        'bottom',
        'bottom-left',
        'bottom-right',
        'left',
        'left-top',
        'left-bottom',
        'right',
        'right-top',
        'right-bottom'
      ],
      selected: 'top'
    };
    this.init();
  }

  init(): void {
    // selecter
    this.booksByAuthor = {};
    this.booksByAuthor['St Exupery'] = [{title: 'The little prince'}];
    this.booksByAuthor['V. Hugo'] = [{title: 'Les miserables'}, {title: 'The Hunchback of Notre-Dame'}];
    this.booksByAuthor['A. Dumas'] = [{title: 'The count of Monte Cristo'}, {title: 'The Three Musketeers'}];


    this.button2Disabled = true;

    this.listItemsDocs = ['Document1', 'Document2', 'Document3', 'Document4', 'Document5'];

    this.listItemsTasks = [{name : 'Task 1', done: false}, {name : 'Task 2', done: true}, {name : 'Task 3', done: false},
      {name : 'Task 4', done: true}, {name : 'Task 5', done: false}];

    // number spinner
    this.number = 0;
    this.numberIsChanged = 0;
  }

  /**
   * Changes search part of URL.
   *
   * @param {number} tabIndex
   */
  onSelectTab(tabIndex?: number): void {
    let param: { tab?: string } = {};
    if (!angular.isUndefined(tabIndex)) {
      param.tab = Tab[tabIndex];
    }
    if (angular.isUndefined(this.$location.search().tab)) {
      this.$location.replace();
    }
    this.$location.search(param);
  }

  toggleDisabled2() {
    this.button2Disabled = !this.button2Disabled;
  }

  isToggleDisabled2() {
    return this.button2Disabled;
  }

  numberChanged(): void {
    this.numberIsChanged++;
  }

}
