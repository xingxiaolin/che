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
 * Defines a directive for the Git project import.
 *
 * @author Oleksii Kurinnyi
 */
export class ImportGitProject implements ng.IDirective {
  restrict: string = 'E';
  templateUrl: string = 'app/workspaces/create-workspace/project-source-selector/add-import-project/import-git-project/import-git-project.html';
  replace: boolean = true;

  controller: string = 'ImportGitProjectController';
  controllerAs: string = 'importGitProjectController';

  bindToController: boolean = true;

  scope: {
    [propName: string]: string;
  };

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor() {
    this.scope = {};
  }

}
