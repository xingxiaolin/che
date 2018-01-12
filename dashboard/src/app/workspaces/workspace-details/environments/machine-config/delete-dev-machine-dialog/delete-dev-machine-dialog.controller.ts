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
import {IMachinesListItem, WorkspaceMachineConfigController} from '../machine-config.controller';

/**
 * This class is handling the controller for deleting machines dialog.
 *
 * @author Oleksii Kurinnyi
 */
export class DeleteDevMachineDialogController {
  /**
   * Material design Dialog service.
   */
  private $mdDialog: ng.material.IDialogService;
  /**
   * Parent controller.
   */
  private callbackController: WorkspaceMachineConfigController;
  /**
   * Current machine.
   * Passed from parent controller.
   */
  private machine: IMachinesListItem;
  /**
   * List of machines.
   * Passed from parent controller.
   */
  private machinesList: IMachinesListItem[];
  /**
   * Popup's message.
   */
  private message: string;
  /**
   * true if machine is being deleted.
   */
  private isProcessing: boolean = false;
  /**
   * Machine name which will be configured as dev-machine
   */
  private newDevMachine: string;

  /**
   * Default constructor that is using resource injection
   * @ngInject for Dependency injection
   */
  constructor($mdDialog: ng.material.IDialogService) {
    this.$mdDialog = $mdDialog;
    if (this.machinesList.length === 1) {
      this.message = 'You can\'t delete it without having other machines configured.';
    } else {
      this.message = 'Select the machine to get ws-agent activated:';
    }
  }

  /**
   * Closes this dialog.
   */
  cancel(): void {
    this.$mdDialog.cancel();
  }

  /**
   * Returns list of machines not including current dev machine.
   *
   * @return {IMachinesListItem[]}
   */
  getMachinesList(): IMachinesListItem[] {
    return this.machinesList.filter((machine: IMachinesListItem) => {
      return machine.name !== this.machine.name;
    });
  }

  deleteDevMachine(): void {
    this.isProcessing = true;
    this.callbackController.enableDevByName(this.newDevMachine).then(() => {
      this.$mdDialog.hide();
    }).finally(() => {
      this.isProcessing = false;
    });
  }
}
