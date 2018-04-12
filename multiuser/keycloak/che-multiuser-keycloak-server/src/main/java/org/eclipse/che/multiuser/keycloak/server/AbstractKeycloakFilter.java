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
package org.eclipse.che.multiuser.keycloak.server;

import static org.eclipse.che.multiuser.machine.authentication.shared.Constants.MACHINE_TOKEN_KIND;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.security.PublicKey;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.che.multiuser.machine.authentication.server.signature.SignatureKeyManager;

/**
 * Base abstract class for the Keycloak-related servlet filters.
 *
 * <p>In particular it defines commnon use-cases when the authentication / multi-user logic should
 * be skipped
 */
public abstract class AbstractKeycloakFilter implements Filter {

  @Inject protected SignatureKeyManager signatureKeyManager;

  /** when a request came from a machine with valid token then auth is not required */
  protected boolean shouldSkipAuthentication(HttpServletRequest request, String token) {
    if (token == null) {
      return false;
    }
    try {
      final PublicKey publicKey = signatureKeyManager.getKeyPair().getPublic();
      final Jwt jwt = Jwts.parser().setSigningKey(publicKey).parse(token);
      return MACHINE_TOKEN_KIND.equals(jwt.getHeader().get("kind"))
          || (request.getRequestURI() != null
              && request.getRequestURI().endsWith("api/keycloak/OIDCKeycloak.js"));
    } catch (ExpiredJwtException | MalformedJwtException | SignatureException ex) {
      // given token is not signed by particular signature key so it must be checked in another way
      return false;
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
