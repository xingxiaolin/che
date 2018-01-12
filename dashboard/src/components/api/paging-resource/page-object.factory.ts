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
import {PageObjectResource} from './page-object-resource';


/**
 * This class is handling the factory for PageObjectResource (a helper class to simplify getting paging resource).
 * PageObjectResource creates resource for getting objects from server side per page.
 *
 * @author Oleksii Orel
 */
export class ChePageObject {

  /**
   * Angular services
   */
  private $resource: ng.resource.IResourceService;
  private $q: ng.IQService;

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($resource: ng.resource.IResourceService, $q: ng.IQService) {
    // keep resource
    this.$resource = $resource;
    this.$q = $q;
  }

  /**
   * Create an instance of PageObjectResource
   * @returns {PageObjectResource}
   */
  createPageObjectResource(url: string, data?: che.IRequestData, objectKey?: string, objectMap?: Map<string, any>): PageObjectResource {
    return new PageObjectResource(url, data ? data : {}, this.$q, this.$resource, objectKey, objectMap);
  }

}
