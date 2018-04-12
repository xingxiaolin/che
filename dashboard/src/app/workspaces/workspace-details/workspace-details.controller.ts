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
import {CheWorkspace, WorkspaceStatus} from '../../../components/api/workspace/che-workspace.factory';
import {CheNotification} from '../../../components/notification/che-notification.factory';
import {WorkspaceDetailsService} from './workspace-details.service';
import IdeSvc from '../../ide/ide.service';
import {WorkspacesService} from '../workspaces.service';
import {ICheEditModeOverlayConfig} from '../../../components/widget/edit-mode-overlay/che-edit-mode-overlay.directive';
import {IEnvironmentManagerMachine} from '../../../components/api/environment/environment-manager-machine';

export  interface IInitData {
  namespaceId: string;
  workspaceName: string;
  workspaceDetails: che.IWorkspace;
}

const TAB: Array<string> = ['Overview', 'Projects', 'Machines', 'Installers', 'Servers', 'Env_Variables', 'Volumes', 'Config', 'SSH', 'Tools'];

/**
 * @ngdoc controller
 * @name workspaces.workspace.details.controller:WorkspaceDetailsController
 * @description This class is handling the controller for workspace to create and edit.
 * @author Ann Shumilova
 * @author Oleksii Kurinnyi
 * @author Oleksii Orel
 */
export class WorkspaceDetailsController {

  static $inject = ['$location', '$log', '$scope', 'lodash', 'cheNotification', 'cheWorkspace', 'ideSvc', 'workspaceDetailsService', 'initData', '$timeout', 'workspacesService'];

  /**
   * Overlay panel configuration.
   */
  editOverlayConfig: ICheEditModeOverlayConfig;
  workspaceDetails: che.IWorkspace;
  workspacesService: WorkspacesService;
  private lodash: any;
  private $scope: ng.IScope;
  private $log: ng.ILogService;
  private $location: ng.ILocationService;
  private $timeout: ng.ITimeoutService;
  private cheNotification: CheNotification;
  private cheWorkspace: CheWorkspace;
  private ideSvc: IdeSvc;
  private workspaceDetailsService: WorkspaceDetailsService;
  private loading: boolean = false;
  private selectedTabIndex: number;
  private namespaceId: string = '';
  private workspaceId: string = '';
  private workspaceName: string = '';
  private newName: string = '';
  private originWorkspaceDetails: any = {};
  private workspaceImportedRecipe: che.IRecipe;
  private forms: Map<string, ng.IFormController> = new Map();
  private tab: { [key: string]: string } = {};
  private errorMessage: string = '';
  private tabsValidationTimeout: ng.IPromise<any>;
  private toolsFilter: Function;
  /**
   * There are unsaved changes to apply (with restart) when is't <code>true</code>.
   */
  private unsavedChangesToApply: boolean;

  /**
   * Default constructor that is using resource injection
   */
  constructor($location: ng.ILocationService,
              $log: ng.ILogService,
              $scope: ng.IScope,
              lodash: any,
              cheNotification: CheNotification,
              cheWorkspace: CheWorkspace,
              ideSvc: IdeSvc,
              workspaceDetailsService: WorkspaceDetailsService,
              initData: IInitData,
              $timeout: ng.ITimeoutService,
              workspacesService: WorkspacesService) {
    this.$log = $log;
    this.$scope = $scope;
    this.$timeout = $timeout;
    this.$location = $location;
    this.lodash = lodash;
    this.cheNotification = cheNotification;
    this.cheWorkspace = cheWorkspace;
    this.ideSvc = ideSvc;
    this.workspaceDetailsService = workspaceDetailsService;
    this.workspacesService = workspacesService;

    if (!initData.workspaceDetails) {
      cheNotification.showError(`There is no workspace with name ${initData.workspaceName}`);
      $location.path('/workspaces').search({});
      return;
    }

    this.namespaceId = initData.namespaceId;
    this.workspaceName = initData.workspaceName;
    this.workspaceId = initData.workspaceDetails.id;

    const action = (newWorkspaceDetails: che.IWorkspace) => {
      if (angular.equals(newWorkspaceDetails, this.originWorkspaceDetails)) {
        return;
      }

      this.originWorkspaceDetails = angular.copy(newWorkspaceDetails);
      if (this.unsavedChangesToApply === false) {
        this.workspaceDetails = angular.copy(newWorkspaceDetails);
      }
      this.checkEditMode();
    };
    this.cheWorkspace.subscribeOnWorkspaceChange(initData.workspaceDetails.id, action);

    this.originWorkspaceDetails = angular.copy(initData.workspaceDetails);
    this.workspaceDetails = angular.copy(initData.workspaceDetails);
    this.checkEditMode();

    this.updateTabs();

    this.updateSelectedTab(this.$location.search().tab);
    const searchDeRegistrationFn = $scope.$watch(() => {
      return $location.search().tab;
    }, (tab: string) => {
      if (angular.isDefined(tab)) {
        this.updateSelectedTab(tab);
      }
    }, true);
    $scope.$on('$destroy', () => {
      this.cheWorkspace.unsubscribeOnWorkspaceChange(this.workspaceId, action);
      searchDeRegistrationFn();
    });

    this.editOverlayConfig = {
      visible: false,
      disabled: false,
      message: {
        content: this.getOverlayMessage(),
        visible: false
      },
      applyButton: {
        action: () => {
          this.applyConfigChanges();
        },
        disabled: this.workspaceDetailsService.getRestartToApply(this.workspaceId) === false,
        title: 'Apply'
      },
      saveButton: {
        action: () => {
          this.saveConfigChanges();
        },
        title: 'Save',
        disabled: false
      },
      cancelButton: {
        action: () => {
          this.cancelConfigChanges();
        }
      }
    };

    this.toolsFilter = (machine: IEnvironmentManagerMachine) => {
      let serverAttributes = this.lodash.pluck(machine.servers, 'attributes');
      let types = this.lodash.pluck(serverAttributes, 'type');
      return types.indexOf('ide') >= 0;
    };
  }

  /**
   * Returns `true` if the recipe of default environment of the workspace has supported recipe type
   *
   * @returns {boolean}
   */
  get isSupported(): boolean {
    return this.workspacesService.isSupported(this.workspaceDetails);
  }

  /**
   * Update tabs.
   */
  updateTabs(): void {
    this.tab = {};
    TAB.forEach((tab: string, $index: number) => {
      const index = $index.toString();
      this.tab[tab] = index;
      this.tab[index] = tab;
    });
  }

  /**
   * Add a new tab.
   *
   * @param tab
   */
  addTab(tab: string): void {
    if (this.tab[tab]) {
      return;
    }
    const pos = (Object.keys(this.tab).length / 2).toString();
    this.tab[tab] = pos;
    this.tab[pos] = tab;
  }

  /**
   * Update selected tab index by search part of URL.
   *
   * @param {string} tab
   */
  updateSelectedTab(tab: string): void {
    const value = this.tab[tab];
    if (angular.isDefined(value)) {
      this.selectedTabIndex = parseInt(value, 10);
    }
  }

  /**
   * Changes search part of URL.
   *
   * @param {number} tabIndex
   */
  onSelectTab(tabIndex?: number): void {
    let param: { tab?: string } = {};
    if (angular.isDefined(tabIndex)) {
      param.tab = this.tab[tabIndex.toString()];
    }
    if (angular.isUndefined(this.$location.search().tab)) {
      this.$location.replace().search(param);
    } else {
      this.$location.search(param);
    }
  }

  /**
   * Returns current status of workspace.
   *
   * @returns {string}
   */
  getWorkspaceStatus(): string {
    return this.workspaceDetailsService.getWorkspaceStatus(this.workspaceId);
  }

  /**
   * Returns workspace details pages (tabs, example - projects)
   *
   * @returns {*}
   */
  getPages(): any {
    return this.workspaceDetailsService.getPages();
  }

  /**
   * Returns workspace details section.
   *
   * @returns {*}
   */
  getSections(): any {
    return this.workspaceDetailsService.getSections();
  }

  /**
   * Callback when workspace config has been changed in editor.
   *
   * @param config {che.IWorkspaceConfig} workspace config
   */
  updateWorkspaceConfigImport(config: che.IWorkspaceConfig): void {
    if (!config) {
      return;
    }
    if (angular.equals(this.workspaceDetails.config, config)) {
      return;
    }

    if (this.newName !== config.name) {
      this.newName = config.name;
    }
    if (!config.environments[config.defaultEnv]) {
      return;
    }
    this.workspaceDetails.config = config;
    this.workspaceImportedRecipe = config.environments[config.defaultEnv].recipe;
    this.checkEditMode();
  }

  /**
   * This method checks form validity on each tab and returns <code>true</code> if
   * all forms are valid.
   *
   * @returns {string[]} list of names of failed tabs.
   */
  checkForFailedTabs(): string[] {
    const failTabs = [];
    const tabs = Object.keys(this.tab).filter((tabKey: string) => {
      return !isNaN(parseInt(tabKey, 10));
    });
    tabs.forEach((tabKey: string) => {
      if (this.checkFormsNotValid(tabKey)) {
        failTabs.push(this.tab[tabKey]);
      }
    });

    return failTabs;
  }

  /**
   * Builds and returns message for edit-mode-overlay component.
   *
   * @param {string[]=} failedTabs list of names of failed tabs.
   * @returns {string}
   */
  getOverlayMessage(failedTabs?: string[]): string {
    if (this.isSupported === false) {
      return `Current infrastructure doesn't support this workspace recipe type.`;
    }

    if (failedTabs && failedTabs.length > 0) {
      const url = this.$location.absUrl().split('?')[0];
      let message = `<i class="error fa fa-exclamation-circle"
          aria-hidden="true"></i>&nbsp;Impossible to save and apply the configuration. Errors in `;
      message += failedTabs.map((tab: string) => {
        return `<a href='${url}?tab=${tab}'>${tab}</a>`;
      }).join(', ');

      return message;
    }

    return 'Your workspace will be restarted if you click Apply button.';
  }

  /**
   * Updates config of edit-mode-overlay component.
   *
   * @param {boolean} configIsDiffer <code>true</code> if config is differ
   * @param {string[]=} failedTabs list of names of failed tabs.
   */
  updateEditModeOverlayConfig(configIsDiffer: boolean, failedTabs?: string[]): void {
    const formIsValid = !failedTabs || failedTabs.length === 0;

    // panel
    this.editOverlayConfig.disabled = !formIsValid || this.loading;
    this.editOverlayConfig.visible = configIsDiffer || this.workspaceDetailsService.getRestartToApply(this.workspaceId);

    // 'save' button
    this.editOverlayConfig.saveButton.disabled = !this.isSupported || !configIsDiffer;

    // 'apply' button
    this.editOverlayConfig.applyButton.disabled = !this.isSupported
      || (!this.unsavedChangesToApply && !this.workspaceDetailsService.getRestartToApply(this.workspaceId));

    // 'cancel' button
    this.editOverlayConfig.cancelButton.disabled = !configIsDiffer;

    // message content
    this.editOverlayConfig.message.content = this.getOverlayMessage(failedTabs);

    // message visibility
    this.editOverlayConfig.message.visible = !this.isSupported
      || failedTabs.length > 0
      || this.unsavedChangesToApply
      || this.workspaceDetailsService.getRestartToApply(this.workspaceId);
  }

  /**
   * Checks editing mode for workspace config.
   */
  checkEditMode(restartToApply?: boolean): ng.IPromise<any> {
    if (!this.originWorkspaceDetails || !this.workspaceDetails) {
      return;
    }

    if (this.tabsValidationTimeout) {
      this.$timeout.cancel(this.tabsValidationTimeout);
    }

    return this.tabsValidationTimeout = this.$timeout(() => {
      const configIsDiffer = !angular.equals(this.originWorkspaceDetails.config, this.workspaceDetails.config);

      // the workspace should be restarted only if its status is STARTING or RUNNING
      if (this.getWorkspaceStatus() === WorkspaceStatus[WorkspaceStatus.STARTING] || this.getWorkspaceStatus() === WorkspaceStatus[WorkspaceStatus.RUNNING]) {
        this.unsavedChangesToApply = configIsDiffer && (this.unsavedChangesToApply || !!restartToApply);
      } else {
        this.unsavedChangesToApply = false;
      }

      // check for failed tabs
      const failedTabs = this.checkForFailedTabs();
      // update overlay
      this.updateEditModeOverlayConfig(configIsDiffer, failedTabs);
      // publish changes
      this.workspaceDetailsService.publishWorkspaceChange(this.workspaceDetails);
    }, 500);
  }

  /**
   * Applies workspace config changes and restarts the workspace.
   */
  applyConfigChanges(): void {
    this.editOverlayConfig.disabled = true;

    this.loading = true;
    this.$scope.$broadcast('edit-workspace-details', {status: 'saving'});

    this.workspaceDetailsService.applyConfigChanges(this.workspaceDetails)
      .then(() => {
        this.workspaceDetailsService.removeRestartToApply(this.workspaceId);
        this.unsavedChangesToApply = false;

        this.cheNotification.showInfo('Workspace updated.');
        this.$scope.$broadcast('edit-workspace-details', {status: 'saved'});

        return this.cheWorkspace.fetchWorkspaceDetails(this.originWorkspaceDetails.id).then(() => {
          this.$location.path('/workspace/' + this.namespaceId + '/' + this.workspaceDetails.config.name).search({tab: this.tab[this.selectedTabIndex]});
        });
      })
      .catch((error: any) => {
        this.$scope.$broadcast('edit-workspace-details', {status: 'failed'});
        this.cheNotification.showError('Update workspace failed.', error);
        return this.checkEditMode(true);
      })
      .finally(() => {
        this.loading = false;
      });
  }

  /**
   * Updates workspace with new config.
   */
  saveConfigChanges(): void {
    this.editOverlayConfig.disabled = true;

    this.loading = true;
    this.$scope.$broadcast('edit-workspace-details', {status: 'saving'});

    this.workspaceDetailsService.saveConfigChanges(this.workspaceDetails)
      .then(() => {
        if (this.unsavedChangesToApply) {
          this.workspaceDetailsService.addRestartToApply(this.workspaceId);
        } else {
          this.workspaceDetailsService.removeRestartToApply(this.workspaceId);
        }
        this.unsavedChangesToApply = false;

        this.cheNotification.showInfo('Workspace updated.');
        this.$scope.$broadcast('edit-workspace-details', {status: 'saved'});

        return this.cheWorkspace.fetchWorkspaceDetails(this.originWorkspaceDetails.id).then(() => {
          this.$location.path('/workspace/' + this.namespaceId + '/' + this.workspaceDetails.config.name).search({tab: this.tab[this.selectedTabIndex]});
        });
      })
      .catch((error: any) => {
        this.$scope.$broadcast('edit-workspace-details', {status: 'failed'});
        const errorMessage = 'Cannot update workspace configuration.';
        this.cheNotification.showError(error && error.data && error.data.message ? error.data.message : errorMessage);
      })
      .finally(() => {
        this.loading = false;

        return this.checkEditMode();
      });
  }

  /**
   * Cancels workspace config changes that weren't stored
   */
  cancelConfigChanges(): void {
    this.editOverlayConfig.disabled = true;
    this.unsavedChangesToApply = false;

    this.workspaceDetails = angular.copy(this.originWorkspaceDetails);

    this.checkEditMode();

    this.$scope.$broadcast('edit-workspace-details', {status: 'cancelled'});
  }

  runWorkspace(): ng.IPromise<any> {
    this.errorMessage = '';

    return this.workspaceDetailsService.runWorkspace(this.workspaceDetails).catch((error: any) => {
      this.errorMessage = error.message;
    });
  }

  stopWorkspace(): ng.IPromise<any> {
    return this.workspaceDetailsService.stopWorkspace(this.workspaceDetails.id);
  }

  /**
   * Register form for corresponding tab.
   *
   * @param tabIndex {string}
   * @param form
   */
  setForm(tabIndex: string, form: ng.IFormController): void {
    this.forms.set(tabIndex, form);
  }

  /**
   * Returns false if all forms from specified tabs are valid
   *
   * @param tabIndex {string}
   * @returns {Boolean}
   */
  checkFormsNotValid(tabIndex: string): boolean {
    const form = this.forms.get(tabIndex);
    return form && form.$invalid;
  }

  /**
   * Returns true when 'Save' button should be disabled
   *
   * @returns {boolean}
   */
  isSaveButtonDisabled(): boolean {
    const tabs = Object.keys(this.tab).filter((tabKey: string) => {
      return !isNaN(parseInt(tabKey, 10));
    });

    return tabs.some((tabKey: string) => this.checkFormsNotValid(tabKey));
  }
}
