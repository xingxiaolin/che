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
import {NamespaceSelectorSvc} from './namespace-selector.service';

/**
 * This class is handling the controller for namespace selector section.
 *
 * @author Oleksii Kurinnyi
 */
export class NamespaceSelectorController {
  /**
   * Namespace selector service.
   */
  namespaceSelectorSvc: NamespaceSelectorSvc;
  /**
   * Callback provided by parent controller. It should be called when namespace is changed.
   */
  onNamespaceChange: (data: {namespaceId: string}) => void;

  /**
   * Default constructor that is using resource injection
   * @ngInject for Dependency injection
   */
  constructor(namespaceSelectorSvc: NamespaceSelectorSvc) {
    this.namespaceSelectorSvc = namespaceSelectorSvc;
  }

  /**
   * Returns list of labels of namespaces
   *
   * @return {[string,string,string,string,string]}
   */
  getNamespaceLabels(): string[] {
    return this.namespaceSelectorSvc.getNamespaceLabels();
  }

  getNamespaceInfo(): string {
    const namespaceId = this.namespaceSelectorSvc.getNamespaceId();
    return this.namespaceSelectorSvc.getNamespaceInfoById(namespaceId);
  }

  /**
   * Returns namespaces empty message if set.
   *
   * @returns {string}
   */
  getNamespaceEmptyMessage(): string {
    return this.namespaceSelectorSvc.getNamespaceEmptyMessage();
  }

  /**
   * Returns the list of available namespaces.
   *
   * @returns {Array<che.INamespace>} array of namespaces
   */
  getNamespaces(): Array<che.INamespace> {
    return this.namespaceSelectorSvc.getNamespaces();
  }

  /**
   * Callback which is called when namespaces is changed.
   *
   * @param {string} label a namespace label
   */
  changeNamespace(label: string): void {
    this.namespaceSelectorSvc.onNamespaceChanged(label);

    const namespaceId = this.namespaceSelectorSvc.getNamespaceId();
    this.onNamespaceChange({namespaceId: namespaceId});
  }

}
