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
import {StackValidationService} from './stack-validation.service';

/**
 * This class is handling the data for import stack
 *
 * @author Oleksii Orel
 */
export class ImportStackService {

  static $inject = ['stackValidationService'];

  private stackValidationService: StackValidationService;
  private stack: che.IStack;

  /**
   * Default constructor that is using resource
   */
  constructor(stackValidationService: StackValidationService) {
    this.stackValidationService = stackValidationService;

    this.stack = {} as che.IStack;
  }

  /**
   * Sets imported stack.
   *
   * @param stack {che.IStack}
   */
  setStack(stack: che.IStack): void {
    this.stack = stack;
  }

  /**
   * Returns imported stack.
   *
   * @returns {che.IStack}
   */
  getStack(): che.IStack {
    return this.stack;
  }

  /**
   * Returns valid status of the stack.
   *
   * @returns {boolean}
   */
  isValid(): boolean {
    return this.stackValidationService.getStackValidation(this.stack).isValid;
  }
}
