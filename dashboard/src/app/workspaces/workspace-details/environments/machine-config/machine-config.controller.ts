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
import {ConfirmDialogService} from '../../../../../components/service/confirm-dialog/confirm-dialog.service';
import {EnvironmentManager} from '../../../../../components/api/environment/environment-manager';
import {IEnvironmentManagerMachine} from '../../../../../components/api/environment/environment-manager-machine';

export interface IMachinesListItem extends che.IWorkspaceRuntimeMachine {
  name: string;
}

/**
 * @ngdoc controller
 * @name workspace.details.controller:WorkspaceMachineConfigController
 * @description This class is handling the controller for machine config
 * @author Oleksii Kurinnyi
 */
export class WorkspaceMachineConfigController {
  $mdDialog: ng.material.IDialogService;
  $q: ng.IQService;
  $timeout: ng.ITimeoutService;
  lodash: _.LoDashStatic;

  timeoutPromise;

  environmentManager: EnvironmentManager;
  machine: IEnvironmentManagerMachine;
  machineConfig: any;
  machinesList: IMachinesListItem[];
  machineName: string;
  newDev: boolean;
  newRam: number;

  machineDevOnChange: Function;
  machineConfigOnChange: Function;
  machineNameOnChange: Function;
  machineOnDelete: Function;
  machineSourceOnChange: Function;

  private confirmDialogService: ConfirmDialogService;
  private newImage: string;

  /**
   * Default constructor that is using resource injection
   * @ngInject for Dependency injection
   */
  constructor($mdDialog: ng.material.IDialogService, $q: ng.IQService, $scope: ng.IScope, $timeout: ng.ITimeoutService, lodash: _.LoDashStatic, confirmDialogService: ConfirmDialogService) {
    this.$mdDialog = $mdDialog;
    this.$q = $q;
    this.$timeout = $timeout;
    this.lodash = lodash;
    this.confirmDialogService = confirmDialogService;

    this.timeoutPromise = null;
    $scope.$on('$destroy', () => {
      if (this.timeoutPromise) {
        $timeout.cancel(this.timeoutPromise);
      }
    });

    this.init();
  }

  /**
   * Sets initial values
   */
  init(): void {
    this.machine = this.lodash.find(this.machinesList, (machine: any) => {
      return machine.name === this.machineName;
    });

    this.machineConfig = {
      source: this.environmentManager.getSource(this.machine),
      isDev: this.environmentManager.isDev(this.machine),
      memoryLimitBytes: this.environmentManager.getMemoryLimit(this.machine),
      servers: this.environmentManager.getServers(this.machine),
      installers: this.environmentManager.getAgents(this.machine),
      canEditEnvVariables: this.environmentManager.canEditEnvVariables(this.machine),
      envVariables: this.environmentManager.getEnvVariables(this.machine)
    };

    this.newDev = this.machineConfig.isDev;

    this.newRam = this.machineConfig.memoryLimitBytes;

    this.newImage = this.machineConfig.source && this.machineConfig.source.image ? this.machineConfig.source.image : null;
  }

  /**
   * Modifies agents list in order to add or remove 'ws-agent'
   */
  enableDev(): void {
    if (this.machineConfig.isDev === this.newDev) {
      return;
    }

    this.machineDevOnChange({name: this.machineName});
  }

  /**
   * For specified machine it adds ws-agent to agents list.
   * @param machineName
   */
  enableDevByName(machineName: string): ng.IPromise<any> {
    return this.machineDevOnChange({name: machineName});
  }

  /**
   * Updates amount of RAM for machine after a delay
   * @param isFormValid {boolean}
   */
  updateRam(isFormValid: boolean): void {
    this.$timeout.cancel(this.timeoutPromise);

    if (!isFormValid || this.machineConfig.memoryLimitBytes === this.newRam) {
      return;
    }

    this.timeoutPromise = this.$timeout(() => {
      this.environmentManager.setMemoryLimit(this.machine, this.newRam);

      this.doUpdateConfig();
    }, 1000);
  }

  /**
   * Callback which is called in order to update list of servers
   * @returns {ng.IPromise<any>}
   */
  updateServers(): ng.IPromise<any> {
    this.environmentManager.setServers(this.machine, this.machineConfig.servers);
    return this.doUpdateConfig();
  }

  /**
   * Callback which is called in order to update list of agents
   * @returns {Promise}
   */
  updateAgents(): ng.IPromise<any> {
    this.environmentManager.setAgents(this.machine, this.machineConfig.installers);
    return this.doUpdateConfig();
  }

  /**
   * Callback which is called in order to update list of environment variables
   * @returns {Promise}
   */
  updateEnvVariables(): void {
    this.environmentManager.setEnvVariables(this.machine, this.machineConfig.envVariables);
    this.doUpdateConfig();
    this.init();
  }

  /**
   * Calls parent controller's callback to update machine config
   * @returns {ng.IPromise<any>}
   */
  doUpdateConfig(): ng.IPromise<any> {
    return this.machineConfigOnChange();
  }

  /**
   * Show dialog to edit machine name
   * @param $event {MouseEvent}
   */
  showEditDialog($event: MouseEvent): void {
    let machineNames = this.machinesList.map((machine: IMachinesListItem) => {
      return machine.name;
    });

    this.$mdDialog.show({
      targetEvent: $event,
      controller: 'EditMachineNameDialogController',
      controllerAs: 'editMachineNameDialogController',
      bindToController: true,
      clickOutsideToClose: true,
      locals: {
        name: this.machineName,
        machineNames: machineNames,
        callbackController: this
      },
      templateUrl: 'app/workspaces/workspace-details/environments/machine-config/edit-machine-name-dialog/edit-machine-name-dialog.html'
    });
  }

  /**
   * Updates machine name
   * @param newMachineName {string} new machine name
   */
  updateMachineName(newMachineName: string): ng.IPromise<any> {
    if (this.machineName === newMachineName) {
      let defer = this.$q.defer();
      defer.resolve();
      return defer.promise;
    }

    return this.machineNameOnChange({
      oldName: this.machineName,
      newName: newMachineName
    });
  }

  /**
   * Deletes machine
   */
  deleteMachine($event: MouseEvent): void {
    let promise;
    if (!this.machineConfig.isDev) {
      promise = this.confirmDialogService.showConfirmDialog('Remove machine', 'Would you like to delete this machine?', 'Delete');
    } else {
      promise = this.showDeleteDevMachineDialog($event);
    }

    promise.then(() => {
      this.machineOnDelete({
        name: this.machineName
      });
      this.init();
    });
  }

  /**
   * Shows confirmation popup before machine to delete
   *
   * @param {MouseEvent} $event
   * @returns {ng.IPromise<any>}
   */
  showDeleteDevMachineDialog($event: MouseEvent): ng.IPromise<any> {
    return this.$mdDialog.show({
      targetEvent: $event,
      controller: 'DeleteDevMachineDialogController',
      controllerAs: 'deleteDevMachineDialogController',
      bindToController: true,
      clickOutsideToClose: true,
      locals: {
        machinesList: this.machinesList,
        machine: this.machine,
        callbackController: this
      },
      templateUrl: 'app/workspaces/workspace-details/environments/machine-config/delete-dev-machine-dialog/delete-dev-machine-dialog.html'
    });
  }


  /**
   * Change machine's source image
   * @param {string} newImage
   */
  changeSource(newImage: string): void {
    this.environmentManager.setSource(this.machine, newImage);
    this.doUpdateConfig();
  }

}
