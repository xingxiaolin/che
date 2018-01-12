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

/* exported PluginFilter */

import {AdminPluginsCtrl} from './plugins.controller';
import {PluginsFilter} from './plugins-filter';
export class AdminsPluginsConfig {

  constructor(register) {
    register.controller('AdminPluginsCtrl', AdminPluginsCtrl);

    new PluginsFilter(register);

    // config routes
    register.app.config(function ($routeProvider) {
      $routeProvider.accessWhen('/admin/plugins', {
        templateUrl: 'app/admin/plugins/plugins.html',
        controller: 'AdminPluginsCtrl',
        controllerAs: 'adminPluginsCtrl'
      });
    })
    ;
  }
}


