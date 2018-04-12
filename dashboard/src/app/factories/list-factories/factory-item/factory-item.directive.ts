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
 * Defines a directive for factory item in list.
 * @author Oleksii Orel
 */
export class CheFactoryItem implements ng.IDirective {
  restrict: string = 'E';

  templateUrl: string = 'app/factories/list-factories/factory-item/factory-item.html';
  replace = false;

  controller: string = 'FactoryItemController';
  controllerAs: string = 'factoryItemController';

  bindToController: boolean = true;

  // we require ngModel as we want to use it inside our directive
  require: Array<string> = ['ngModel'];
  scope: {
    [propName: string]: string;
  };

  /**
   * Default constructor.
   */
  constructor() {
    this.scope = {
      factory: '=cdvyFactory',
      isChecked: '=cdvyChecked',
      isSelectable: '=cdvyIsSelectable',
      isSelect: '=?ngModel',
      onCheckboxClick: '&?cdvyOnCheckboxClick'
    };

  }

}
