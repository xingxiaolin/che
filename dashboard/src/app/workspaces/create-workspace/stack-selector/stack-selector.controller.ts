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
import {CheStack} from '../../../../components/api/che-stack.factory';
import {CheEnvironmentRegistry} from '../../../../components/api/environment/che-environment-registry.factory';
import {EnvironmentManager} from '../../../../components/api/environment/environment-manager';
import {StackSelectorScope} from './stack-selector-scope.enum';
import {StackSelectorSvc} from './stack-selector.service';
import {CheBranding} from '../../../../components/branding/che-branding.factory';
import {ConfirmDialogService} from '../../../../components/service/confirm-dialog/confirm-dialog.service';

/**
 * @ngdoc controller
 * @name workspaces.stack-selector.controller:StackSelector
 * @description This class is handling the controller of stack selector.
 * @author Oleksii Kurinnyi
 */
export class StackSelectorController {
  /**
   * Filter service.
   */
  $filter: ng.IFilterService;
  /**
   * Location service.
   */
  $location: ng.ILocationService;
  /**
   * Dialog service.
   */
  $mdDialog: ng.material.IDialogService;
  /**
   * Lodash library.
   */
  lodash: any;
  /**
   * Stack API interaction.
   */
  cheStack: CheStack;
  /**
   * Environments manager.
   */
  cheEnvironmentRegistry: CheEnvironmentRegistry;
  /**
   * Confirm dialog service.
   */
  confirmDialogService: ConfirmDialogService;
  /**
   * Stack selector service.
   */
  stackSelectorSvc: StackSelectorSvc;
  /**
   * Stack scopes.
   */
  scope: Object;
  /**
   * If <code>true</code> then popover with filters should be shown.
   */
  showFilters: boolean;
  /**
   * Current scope to filter stacks.
   */
  selectedScope: number;
  /**
   * Search string to filter stacks.
   */
  searchString: string;
  /**
   * Field name to order stacks.
   */
  stackOrderBy: string;
  /**
   * The list of all stacks.
   */
  stacks: che.IStack[];
  /**
   * Lists of stacks by scope.
   */
  stacksByScope: {
    [scope: number]: Array<che.IStack>
  };
  /**
   * The list of filtered stacks.
   */
  stacksFiltered: che.IStack[];
  /**
   * Environment managers by recipe type.
   */
  environmentManagers: {
    [recipeType: string]: EnvironmentManager
  };
  /**
   * Selected stack ID.
   */
  selectedStackId: string;
  /**
   * Stack's icons
   */
  stackIconLinks: {
    [stackId: string]: string
  };
  /**
   * Stack's machines.
   */
  stackMachines: {
    [stackId: string]: Array<{[machineProp: string]: string|number}>
  };
  /**
   * Callback which should be called when stack is selected.
   */
  onStackSelect: (data: {stackId: string}) => void;
  /**
   * The list of tags selected by user.
   */
  private selectedTags: Array<string>;
  /**
   * The list of tags of visible stacks.
   */
  private allStackTags: Array<string>;
  /**
   * The default stack to be preselected (comes from configuration).
   */
  private defaultStack: string;
  /**
   * The priority stacks to be placed before others (comes from configuration).
   */
  private priorityStacks: Array<string>;

  /**
   * Default constructor that is using resource injection
   * @ngInject for Dependency injection
   */
  constructor($filter: ng.IFilterService, $mdDialog: ng.material.IDialogService, lodash: any, cheStack: CheStack,
              confirmDialogService: ConfirmDialogService, $location: ng.ILocationService, cheBranding: CheBranding,
              cheEnvironmentRegistry: CheEnvironmentRegistry, stackSelectorSvc: StackSelectorSvc) {
    this.$filter = $filter;
    this.$location = $location;
    this.$mdDialog = $mdDialog;
    this.lodash = lodash;
    this.cheStack = cheStack;
    this.cheEnvironmentRegistry = cheEnvironmentRegistry;
    this.stackSelectorSvc = stackSelectorSvc;
    this.confirmDialogService = confirmDialogService;

    this.priorityStacks = cheBranding.getWorkspace().priorityStacks;
    this.defaultStack = cheBranding.getWorkspace().defaultStack;
    this.scope = StackSelectorScope;
    this.showFilters = false;
    this.selectedScope = StackSelectorScope.QUICK_START;
    this.stackOrderBy = 'name';
    this.stacksByScope = {};
    this.stacksFiltered = [];
    this.environmentManagers = {};
    this.stackIconLinks = {};
    this.stackMachines = {};
    this.selectedTags = [];
    this.allStackTags = [];

    this.stacks = this.stackSelectorSvc.getStacks();
    this.updateMachines();
    this.buildStacksListsByScope();
    this.buildFilteredList();
  }

  /**
   * Update filtered stack keys depends on tags.
   *
   * @param tags {Array<string>} the list of tags to filter stacks
   */
  onTagsChanges(tags?: Array<string>): void {
    this.selectedTags = tags;
    this.buildFilteredList();
  }

  /**
   * For each stack get machines and for each machine cast memory limit to GB.
   * Get stack icons.
   */
  updateMachines(): void {
    this.stacks.forEach((stack: che.IStack) => {
      // get icon link
      const findLink = this.lodash.find(stack.links, (link: che.IStackLink) => {
        return link.rel === 'get icon link';
      });
      if (findLink) {
        this.stackIconLinks[stack.id] = findLink.href;
      }

      this.stackMachines[stack.id] = [];
      if (stack.workspaceConfig) {
        // get machines memory limits
              const defaultEnv = stack.workspaceConfig.defaultEnv,
                    environment = stack.workspaceConfig.environments[defaultEnv],
                    environmentManager = this.getEnvironmentManager(environment.recipe.type);
              if (environmentManager) {
                let machines = environmentManager.getMachines(environment);

                machines.forEach((machine: any) => {
                  this.stackMachines[stack.id].push({
                    name: machine.name,
                    memoryLimitBytes: environmentManager.getMemoryLimit(machine)
                  });
                });
              }
      }
    });
  }

  /**
   * Build lists of stacks separated by scope.
   */
  buildStacksListsByScope(): void {
    const scopes = StackSelectorScope.values();

    scopes.forEach((scope: StackSelectorScope) => {
      this.stacksByScope[scope] = this.$filter('stackScopeFilter')(this.stacks, scope, this.stackMachines);
    });
  }

  /**
   * Returns environment manager specified by recipe type.
   *
   * @param recipeType {string} recipe type
   * @return {EnvironmentManager}
   */
  getEnvironmentManager(recipeType: string): EnvironmentManager {
    if (!this.environmentManagers[recipeType]) {
      this.environmentManagers[recipeType] = this.cheEnvironmentRegistry.getEnvironmentManager(recipeType);
    }

    return this.environmentManagers[recipeType];
  }

  /**
   * Set specified stack ID as selected.
   *
   * @param stackId {string} stack ID
   */
  selectStack(stackId: string): void {
    this.selectedStackId = stackId;

    this.onStackSelect({stackId: stackId});

    this.stackSelectorSvc.onStackSelected(stackId);
  }

  /**
   * Callback on search query has been changed.
   *
   * @param searchString {string}
   */
  searchChanged(searchString: string): void {
    this.searchString = searchString;
    this.buildFilteredList();
  }

  /**
   * Callback on scope has been changed.
   */
  scopeChanged(): void {
    this.buildFilteredList();
  }

  /**
   * Rebuild list of filtered and sorted stacks. Set selected stack if it's needed.
   */
  buildFilteredList(): void {
    this.stacksFiltered.length = 0;
    this.stacksFiltered = this.stacksByScope[this.selectedScope];

    // filter by tags
    this.stacksFiltered = this.$filter('stackTagsFilter')(this.stacksFiltered, this.selectedTags);

    // filter by name
    this.stacksFiltered = this.$filter('stackSearchFilter')(this.stacksFiltered, this.searchString);

    this.stacksFiltered = this.$filter('orderBy')(this.stacksFiltered, this.stackOrderBy);

    if (this.priorityStacks) {
      let priorityStacks = this.lodash.remove(this.stacksFiltered, (stack: che.IStack) => {
        return this.priorityStacks.indexOf(stack.name) >= 0;
      });

      this.stacksFiltered = priorityStacks.concat(this.stacksFiltered);
    }

    this.updateTags();

    if (this.stacksFiltered.length === 0) {
      return;
    }

    if (this.needToSelectStack()) {
      let ids = this.lodash.pluck(this.stacksFiltered, 'id');
      let stackId = (this.defaultStack && ids.indexOf(this.defaultStack) >= 0) ? this.defaultStack : this.stacksFiltered[0].id;
      this.selectStack(stackId);
    }
  }

  /**
   * Handles the adding stack options.
   */
  onAddStack(): void {
    this.confirmDialogService.showConfirmDialog('Create stack', 'Would you like to create a stack from a recipe?', 'Yes', 'No').then(() => {
      this.$mdDialog.show({
        controller: 'BuildStackController',
        controllerAs: 'buildStackController',
        bindToController: true,
        clickOutsideToClose: true,
        locals: {
          callbackController: this
        },
        templateUrl: 'app/stacks/list-stacks/build-stack/build-stack.html'
      });
    }, () => {
      this.$location.path('/stack/create');
    });
  }

  /**
   * Returns <code>true</code> if previously selected stack is hidden by filter.
   */
  private needToSelectStack(): boolean {
    return this.lodash.every(this.stacksFiltered, (stack: che.IStack) => {
      return stack.id !== this.selectedStackId;
    });
  }

  /**
   * Update list of tags which can be selected.
   *
   * @private
   */
  private updateTags(): void {
    this.allStackTags.length = 0;
    this.stacksFiltered.forEach((stack: che.IStack) => {
      this.allStackTags = this.allStackTags.concat(stack.tags);
    });
    this.allStackTags = this.lodash.uniq(this.allStackTags);
  }

}
