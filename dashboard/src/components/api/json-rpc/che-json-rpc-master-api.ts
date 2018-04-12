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
import {CheJsonRpcApiClient} from './che-json-rpc-api-service';
import {ICommunicationClient} from './json-rpc-client';

enum MasterChannels {
  ENVIRONMENT_OUTPUT = <any>'machine/log',
  ENVIRONMENT_STATUS = <any>'machine/statusChanged',
  WS_AGENT_OUTPUT = <any>'installer/log',
  WORKSPACE_STATUS = <any>'workspace/statusChanged',
  ORGANIZATION_STATUS = <any>'organization/statusChanged',
  ORGANIZATION_MEMBERSHIP_STATUS = <any>'organization/membershipChanged'
}

enum MasterScopes {
  ORGANIZATION = <any>'organizationId',
  USER = <any>'userId',
  WORKSPACE = <any>'workspaceId'
}

const SUBSCRIBE: string = 'subscribe';
const UNSUBSCRIBE: string = 'unsubscribe';

/**
 * Client API for workspace master interactions.
 *
 * @author Ann Shumilova
 */
export class CheJsonRpcMasterApi {
  private $log: ng.ILogService;
  private $timeout: ng.ITimeoutService;
  private cheJsonRpcApi: CheJsonRpcApiClient;
  private clientId: string;

  private maxReconnectionAttempts = 5;
  private reconnectionAttemptNumber = 0;
  private reconnectionDelay = 30000;

  constructor (client: ICommunicationClient,
               entrypoint: string,
               $log: ng.ILogService,
               $timeout: ng.ITimeoutService) {
    this.$log = $log;
    this.$timeout = $timeout;

    client.addListener('open', () => this.onConnectionOpen());
    client.addListener('close', () => this.onConnectionClose(entrypoint));

    this.cheJsonRpcApi = new CheJsonRpcApiClient(client);
    this.connect(entrypoint);
  }

  onConnectionOpen(): void {
    if (this.reconnectionAttemptNumber !== 0) {
      this.$log.log('WebSocket connection is opened.');
    }
    this.reconnectionAttemptNumber = 0;
  }

  onConnectionClose(entrypoint: string): void {
    this.$log.warn('WebSocket connection is closed.');
    if (this.reconnectionAttemptNumber === this.maxReconnectionAttempts) {
      this.$log.warn('The maximum number of attempts to reconnect WebSocket has been reached.');
      return;
    }

    this.reconnectionAttemptNumber++;
    // let very first reconnection happens immediately after the connection is closed.
    const delay = this.reconnectionAttemptNumber === 1 ? 0 : this.reconnectionDelay;

    if (delay) {
      this.$log.warn(`WebSocket will be reconnected in ${delay} ms...`);
    }
    this.$timeout(() => {
      this.$log.warn(`WebSocket is reconnecting, attempt #${this.reconnectionAttemptNumber} out of ${this.maxReconnectionAttempts}...`);
      this.connect(entrypoint);
    }, delay);
  }

  /**
   * Opens connection to pointed entrypoint.
   *
   * @param entrypoint
   * @returns {IPromise<IHttpPromiseCallbackArg<any>>}
   */
  connect(entrypoint: string): ng.IPromise<any> {
    if (this.clientId) {
      let clientId = `clientId=${this.clientId}`;
      // in case of reconnection
      // we need to test entrypoint on existing query parameters
      // to add already gotten clientId
      if (/\?/.test(entrypoint) === false) {
        clientId = '?' + clientId;
      } else {
        clientId = '&' + clientId;
      }
      entrypoint += clientId;
    }
    return this.cheJsonRpcApi.connect(entrypoint).then(() => {
      return this.fetchClientId();
    });
  }

  /**
   * Subscribes the environment output.
   *
   * @param workspaceId workspace's id
   * @param machineName machine's name
   * @param callback callback to process event
   */
  subscribeEnvironmentOutput(workspaceId: string, callback: Function): void {
    this.subscribe(MasterChannels.ENVIRONMENT_OUTPUT, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Un-subscribes the pointed callback from the environment output.
   *
   * @param workspaceId workspace's id
   * @param machineName machine's name
   * @param callback callback to process event
   */
  unSubscribeEnvironmentOutput(workspaceId: string, callback: Function): void {
    this.unsubscribe(MasterChannels.ENVIRONMENT_OUTPUT, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Subscribes the environment status changed.
   *
   * @param workspaceId workspace's id
   * @param callback callback to process event
   */
  subscribeEnvironmentStatus(workspaceId: string, callback: Function): void {
    this.subscribe(MasterChannels.ENVIRONMENT_STATUS, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Un-subscribes the pointed callback from environment status changed.
   *
   * @param workspaceId workspace's id
   * @param callback callback to process event
   */
  unSubscribeEnvironmentStatus(workspaceId: string, callback: Function): void {
    this.unsubscribe(MasterChannels.ENVIRONMENT_STATUS, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Subscribes on workspace agent output.
   *
   * @param workspaceId workspace's id
   * @param callback callback to process event
   */
  subscribeWsAgentOutput(workspaceId: string, callback: Function): void {
    this.subscribe(MasterChannels.WS_AGENT_OUTPUT, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Un-subscribes from workspace agent output.
   *
   * @param workspaceId workspace's id
   * @param callback callback to process event
   */
  unSubscribeWsAgentOutput(workspaceId: string, callback: Function): void {
    this.unsubscribe(MasterChannels.WS_AGENT_OUTPUT, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Subscribes to workspace's status.
   *
   * @param workspaceId workspace's id
   * @param callback callback to process event
   */
  subscribeWorkspaceStatus(workspaceId: string, callback: Function): void {
    let statusHandler = (message: any) => {
      if (workspaceId === message.workspaceId) {
        callback(message);
      }
    };
    this.subscribe(MasterChannels.WORKSPACE_STATUS, MasterScopes.WORKSPACE, workspaceId, statusHandler);
  }

  /**
   * Un-subscribes pointed callback from workspace's status.
   *
   * @param workspaceId
   * @param callback
   */
  unSubscribeWorkspaceStatus(workspaceId: string, callback: Function): void {
    this.unsubscribe(MasterChannels.WORKSPACE_STATUS, MasterScopes.WORKSPACE, workspaceId, callback);
  }

  /**
   * Subscribe to organization statuses.
   *
   * @param {string} organizationId organization's id
   * @param {Function} callback handler
   */
  subscribeOrganizationStatus(organizationId: string, callback: Function): void {
    this.subscribe(MasterChannels.ORGANIZATION_STATUS, MasterScopes.ORGANIZATION, organizationId, callback);
  }

  /**
   * Un-subscribe from organization status changes.
   *
   * @param {string} organizationId organization's id
   * @param {Function} callback handler
   */
  unSubscribeOrganizationStatus(organizationId: string, callback?: Function): void {
    this.unsubscribe(MasterChannels.ORGANIZATION_STATUS, MasterScopes.ORGANIZATION, organizationId, callback);
  }

  /**
   * Subscribe to organization membership changes.
   *
   * @param {string} userId user's id to track changes
   * @param {Function} callback handler
   */
  subscribeOrganizationMembershipStatus(userId: string, callback: Function): void {
    this.subscribe(MasterChannels.ORGANIZATION_MEMBERSHIP_STATUS, MasterScopes.USER, userId, callback);
  }

  /**
   * Un-subscribe from organization membership changes.
   *
   * @param {string} userId user's id to untrack changes
   * @param {Function} callback handler
   */
  unSubscribeOrganizationMembershipStatus(userId: string, callback: Function): void {
    this.unsubscribe(MasterChannels.ORGANIZATION_MEMBERSHIP_STATUS, MasterScopes.USER, userId, callback);
  }

  /**
   * Fetch client's id and strores it.
   *
   * @returns {IPromise<TResult>}
   */
  fetchClientId(): ng.IPromise<any> {
    return this.cheJsonRpcApi.request('websocketIdService/getId').then((data: any) => {
      this.clientId = data[0];
    });
  }

  /**
   * Returns client's id.
   *
   * @returns {string} clinet connection identifier
   */
  getClientId(): string {
    return this.clientId;
  }

  /**
   * Performs subscribe to the pointed channel for pointed workspace's ID and callback.
   *
   * @param channel channel to un-subscribe
   * @param _scope the scope of the request
   * @param id instance's id (scope value)
   * @param callback callback
   */
  private subscribe(channel: MasterChannels, _scope: MasterScopes, id: string, callback: Function): void {
    let method: string = channel.toString();
    let masterScope: string = _scope.toString();
    let params = {method: method, scope: {}};
    params.scope[masterScope] = id;
    this.cheJsonRpcApi.subscribe(SUBSCRIBE, method, callback, params);
  }

  /**
   * Performs un-subscribe of the pointed channel by pointed workspace's ID and callback.
   *
   * @param channel channel to un-subscribe
   * @param _scope the scope of the request
   * @param id instance's id (scope value)
   * @param callback callback
   */
  private unsubscribe(channel: MasterChannels, _scope: MasterScopes, id: string, callback: Function): void {
    let method: string = channel.toString();
    let masterScope: string = _scope.toString();
    let params = {method: method, scope: {}};
    params.scope[masterScope] = id;
    this.cheJsonRpcApi.unsubscribe(UNSUBSCRIBE, method, callback, params);
  }
}
