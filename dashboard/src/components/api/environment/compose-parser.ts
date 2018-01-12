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

import {IParser} from './parser';

export interface IComposeServiceRecipe {
  image: string;
  [propName: string]: any;
}

export interface IComposeRecipe {
  services: {
    [serviceName: string]: IComposeServiceRecipe
  };
}

/**
 * Wrapper for jsyaml and simple validator.
 *
 * @author Oleksii Kurinnyi
 */
export class ComposeParser implements IParser {

  /**
   * Parses recipe content
   *
   * @param content {string} recipe content
   * @returns {IComposeRecipe} recipe object
   */
  parse(content: string): IComposeRecipe {
    const recipe = jsyaml.load(content);
    if (recipe && !recipe.services) { // if it is machine recipe
      this.validate({services: recipe});
    } else {
      this.validate(recipe);
    }
    return recipe;
  }

  /**
   * Dumps recipe object.
   *
   * @param recipe {IComposeRecipe} recipe object
   * @returns {string} recipe content
   */

  dump(recipe: IComposeRecipe): string {
    return jsyaml.dump(recipe, {'indent': 1});
  }

  /**
   * Simple validation of recipe.
   *
   * @param recipe {IComposeRecipe}
   */
  private validate(recipe: IComposeRecipe): void {
    if (!recipe.services) {
      throw new TypeError(`Recipe should contain "services" section.`);
    }

    const services = Object.keys(recipe.services);
    services.forEach((serviceName: string) => {
      let serviceFields: string[] = Object.keys(recipe.services[serviceName] || {});
      if (!serviceFields || (serviceFields.indexOf('build') === -1 && serviceFields.indexOf('image') === -1)) {
        throw new TypeError(`Service "${serviceName}" should contain "build" or "image" section.`);
      }
    });
  }

}
