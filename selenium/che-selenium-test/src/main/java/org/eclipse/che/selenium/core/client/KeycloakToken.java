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

import com.google.gson.annotations.SerializedName;

/** @author Mihail Kuznyetsov */
public class KeycloakToken {
  @SerializedName("access_token")
  private String accessToken;

  @SerializedName("expires_in")
  private long expiresIn;

  @SerializedName("refresh_token")
  private String refreshToken;

  private TokenDetails accessDetails;
  private TokenDetails refreshDetails;

  public KeycloakToken() {}

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public TokenDetails getAccessDetails() {
    return accessDetails;
  }

  public void setAccessDetails(TokenDetails accessDetails) {
    this.accessDetails = accessDetails;
  }

  public TokenDetails getRefreshDetails() {
    return refreshDetails;
  }

  public void setRefreshDetails(TokenDetails refreshDetails) {
    this.refreshDetails = refreshDetails;
  }

  static class TokenDetails {
    @SerializedName("exp")
    private long expiresAt;

    @SerializedName("iat")
    private long initialized;

    public long getExpiresAt() {
      return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
      this.expiresAt = expiresAt;
    }

    public long getInitialized() {
      return initialized;
    }

    public void setInitialized(long initialized) {
      this.initialized = initialized;
    }
  }
}
