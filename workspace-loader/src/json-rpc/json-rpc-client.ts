/*
 * Copyright (c) 2018-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
'use strict';
import { IDeffered, Deffered } from './util';
const JSON_RPC_VERSION: string = '2.0';

/**
 * Interface for communication between two entrypoints.
 * The implementation can be through websocket or http protocol.
 */
export interface ICommunicationClient {
  /**
   * Process responses.
   */
  onResponse: Function;
  /**
   * Performs connections.
   *
   * @param entrypoint
   */
  connect(entrypoint: string): Promise<any>;
  /**
   * Close the connection.
   */
  disconnect(): void;
  /**
   * Send pointed data.
   *
   * @param data data to be sent
   */
  send(data: any): void;
}

interface IRequest {
  jsonrpc: string;
  id: string;
  method: string;
  params: any;
}

interface INotification {
  jsonrpc: string;
  method: string;
  params: any;
}

/**
 * This client is handling the JSON RPC requests, responses and notifications.
 *
 * @author Ann Shumilova
 */
export class JsonRpcClient {
  /**
   * Client for performing communications.
   */
  private client: ICommunicationClient;
  /**
   * The list of the pending requests by request id.
   */
  private pendingRequests: Map<string, IDeffered<any>>;
  /**
   * The list of notification handlers by method name.
   */
  private notificationHandlers: Map<string, Array<Function>>;
  private counter: number = 100;

  constructor(client: ICommunicationClient) {
    this.client = client;
    this.pendingRequests = new Map<string, IDeffered<any>>();
    this.notificationHandlers = new Map<string, Array<Function>>();

    this.client.onResponse = (message: any): void => {
      this.processResponse(message);
    };
  }

  /**
   * Performs JSON RPC request.
   *
   * @param method method's name
   * @param params params
   * @returns {IPromise<any>}
   */
  request(method: string, params?: any): Promise<any> {
    let deferred = new Deffered();
    let id: string = (this.counter++).toString();
    this.pendingRequests.set(id, deferred);

    let request: IRequest = {
      jsonrpc: JSON_RPC_VERSION,
      id: id,
      method: method,
      params: params
    };

    this.client.send(request);
    return deferred.promise;
  }

  /**
   * Sends JSON RPC notification.
   *
   * @param method method's name
   * @param params params (optional)
   */
  notify(method: string, params?: any): void {
    let request: INotification = {
      jsonrpc: JSON_RPC_VERSION,
      method: method,
      params: params
    };

    this.client.send(request);
  }

  /**
   * Adds notification handler.
   *
   * @param method method's name
   * @param handler handler to process notification
   */
  public addNotificationHandler(method: string, handler: Function): void {
    let handlers = this.notificationHandlers.get(method);

    if (handlers) {
      handlers.push(handler);
    } else {
      handlers = [handler];
      this.notificationHandlers.set(method, handlers);
    }
  }

  /**
   * Removes notification handler.
   *
   * @param method method's name
   * @param handler handler
   */
  public removeNotificationHandler(method: string, handler: Function): void {
    let handlers = this.notificationHandlers.get(method);

    if (handlers) {
      handlers.splice(handlers.indexOf(handler), 1);
    }
  }

  /**
   * Processes response - detects whether it is JSON RPC response or notification.
   *
   * @param message
   */
  private processResponse(message: any): void {
    if (message.id && this.pendingRequests.has(message.id)) {
      this.processResponseMessage(message);
    } else {
      this.processNotification(message);
    }
  }

  /**
   * Processes JSON RPC notification.
   *
   * @param message message
   */
  private processNotification(message: any): void {
    let method = message.method;
    let handlers = this.notificationHandlers.get(method);
    if (handlers && handlers.length > 0) {
      handlers.forEach((handler: Function) => {
        handler(message.params);
      });
    }
  }

  /**
   * Process JSON RPC response.
   *
   * @param message
   */
  private processResponseMessage(message: any): void {
    let promise = this.pendingRequests.get(message.id);
    if (message.result) {
      promise.resolve(message.result);
      return;
    }
    if (message.error) {
      promise.reject(message.error);
    }
  }
}
