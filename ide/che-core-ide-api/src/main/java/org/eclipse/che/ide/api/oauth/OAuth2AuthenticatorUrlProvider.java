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
package org.eclipse.che.ide.api.oauth;

import com.google.common.base.Joiner;
import com.google.gwt.user.client.Window;
import java.util.List;

/**
 * Constructs URL's to OAUth authentication depending on current host and rest context.
 *
 * @author Vitalii Parfonov
 */
public class OAuth2AuthenticatorUrlProvider {

  private static final String oAuthServicePath = "/oauth/authenticate";

  public static String get(String restContext, String authenticatePath) {
    return restContext + authenticatePath + "&redirect_after_login=" + redirect();
  }

  public static String get(
      String restContext, String providerName, String userId, List<String> scopes) {
    final String scope = Joiner.on(',').join(scopes);

    return restContext
        + oAuthServicePath
        + "?oauth_provider="
        + providerName
        + "&scope="
        + scope
        + "&userId="
        + userId
        + "&redirect_after_login="
        + redirect();
  }

  private static String redirect() {
    return Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/ws/";
  }
}
