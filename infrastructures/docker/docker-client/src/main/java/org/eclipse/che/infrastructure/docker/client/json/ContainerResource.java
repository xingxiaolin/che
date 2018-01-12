/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.infrastructure.docker.client.json;

/** @author andrew00x */
public class ContainerResource {
  private String resource;

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  @Override
  public String toString() {
    return "ContainerResource{" + "resource='" + resource + '\'' + '}';
  }

  // -------------------

  public ContainerResource withResource(String resource) {
    this.resource = resource;
    return this;
  }
}
