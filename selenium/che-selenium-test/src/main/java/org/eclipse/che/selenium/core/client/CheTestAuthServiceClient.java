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
package org.eclipse.che.selenium.core.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Anatolii Bazko */
@Singleton
public class CheTestAuthServiceClient implements TestAuthServiceClient {

  private static final Logger LOG = LoggerFactory.getLogger(CheTestAuthServiceClient.class);

  private final String apiEndpoint;
  private final HttpJsonRequestFactory requestFactory;

  @Inject
  public CheTestAuthServiceClient(String apiEndpoint, HttpJsonRequestFactory requestFactory) {
    this.apiEndpoint = apiEndpoint;
    this.requestFactory = requestFactory;
  }

  @Override
  public String login(String username, String password) throws Exception {
    return username;
  }

  @Override
  public void logout(String authToken) {
    try {
      String apiUrl = apiEndpoint + "auth/logout?token=" + authToken;
      requestFactory.fromUrl(apiUrl).usePostMethod().request();
    } catch (Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
