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


import {FactoryDetailsConfig} from './factory-details/factory-details-config';
import {CreateFactoryConfig} from './create-factory/create-factory-config';
import {LastFactoriesConfig} from './last-factories/last-factories-config';
import {ListFactoriesController} from './list-factories/list-factories.controller';
import {FactoryItemController} from './list-factories/factory-item/factory-item.controller';
import {CheFactoryItem} from './list-factories/factory-item/factory-item.directive';
import {LoadFactoryController} from './load-factory/load-factory.controller';
import {LoadFactoryService} from './load-factory/load-factory.service';

export class FactoryConfig {

  constructor(register: che.IRegisterService) {
    register.controller('ListFactoriesController', ListFactoriesController);

    register.controller('FactoryItemController', FactoryItemController);
    register.directive('cdvyFactoryItem', CheFactoryItem);

    register.controller('LoadFactoryController', LoadFactoryController);
    register.service('loadFactoryService', LoadFactoryService);

    // config routes
    register.app.config(function ($routeProvider) {
      $routeProvider.accessWhen('/factories', {
        title: 'Factories',
        templateUrl: 'app/factories/list-factories/list-factories.html',
        controller: 'ListFactoriesController',
        controllerAs: 'listFactoriesCtrl'
      })
        .accessWhen('/load-factory', {
          title: 'Load Factory',
          templateUrl: 'app/factories/load-factory/load-factory.html',
          controller: 'LoadFactoryController',
          controllerAs: 'loadFactoryController'
        })
      .accessWhen('/load-factory/:id', {
        title: 'Load Factory',
        templateUrl: 'app/factories/load-factory/load-factory.html',
        controller: 'LoadFactoryController',
        controllerAs: 'loadFactoryController'
      });

    });

    // config files
    new FactoryDetailsConfig(register);
    new CreateFactoryConfig(register);
    new LastFactoriesConfig(register);
  }
}

