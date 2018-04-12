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

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.PublicKey;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.che.commons.auth.token.RequestTokenExtractor;
import org.eclipse.che.multiuser.keycloak.shared.KeycloakConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KeycloakAuthenticationFilter extends AbstractKeycloakFilter {
  private static final Logger LOG = LoggerFactory.getLogger(KeycloakAuthenticationFilter.class);

  private String jwksUrl;
  private long allowedClockSkewSec;
  private RequestTokenExtractor tokenExtractor;
  private JwkProvider jwkProvider;

  @Inject
  public KeycloakAuthenticationFilter(
      KeycloakSettings keycloakSettings,
      @Named(KeycloakConstants.ALLOWED_CLOCK_SKEW_SEC) long allowedClockSkewSec,
      RequestTokenExtractor tokenExtractor)
      throws MalformedURLException {
    this.jwksUrl = keycloakSettings.get().get(KeycloakConstants.JWKS_ENDPOINT_SETTING);
    this.allowedClockSkewSec = allowedClockSkewSec;
    this.tokenExtractor = tokenExtractor;
    if (jwksUrl != null) {
      this.jwkProvider = new GuavaCachedJwkProvider(new UrlJwkProvider(new URL(jwksUrl)));
    }
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;

    final String token = tokenExtractor.getToken(request);
    if (shouldSkipAuthentication(request, token)) {
      chain.doFilter(req, res);
      return;
    }

    final String requestURI = request.getRequestURI();
    if (token == null) {
      LOG.debug("No 'Authorization' header for {}", requestURI);
      send403(res);
      return;
    }

    Jws<Claims> jwt;
    try {
      jwt =
          Jwts.parser()
              .setAllowedClockSkewSeconds(allowedClockSkewSec)
              .setSigningKeyResolver(
                  new SigningKeyResolverAdapter() {
                    @Override
                    public Key resolveSigningKey(
                        @SuppressWarnings("rawtypes") JwsHeader header, Claims claims) {
                      try {
                        return getJwtPublicKey(header);
                      } catch (JwkException e) {
                        throw new JwtException(
                            "Error during the retrieval of the public key during JWT token validation",
                            e);
                      }
                    }
                  })
              .parseClaimsJws(token);
      LOG.debug("JWT = ", jwt);
      // OK, we can trust this JWT
    } catch (SignatureException | IllegalArgumentException e) {
      // don't trust the JWT!
      LOG.error("Failed verifying the JWT token", e);
      send403(res);
      return;
    }
    request.setAttribute("token", jwt);
    chain.doFilter(req, res);
  }

  private synchronized PublicKey getJwtPublicKey(JwsHeader<?> header) throws JwkException {
    String kid = header.getKeyId();
    if (kid == null) {
      LOG.warn(
          "'kid' is missing in the JWT token header. This is not possible to validate the token with OIDC provider keys");
      return null;
    }
    String alg = header.getAlgorithm();
    if (alg == null) {
      LOG.warn(
          "'alg' is missing in the JWT token header. This is not possible to validate the token with OIDC provider keys");
      return null;
    }

    if (jwkProvider == null) {
      LOG.warn(
          "JWK provider is not available: This is not possible to validate the token with OIDC provider keys.\n"
              + "Please look into the startup logs to find out the root cause");
      return null;
    }
    Jwk jwk = jwkProvider.get(kid);
    return jwk.getPublicKey();
  }

  private void send403(ServletResponse res) throws IOException {
    HttpServletResponse response = (HttpServletResponse) res;
    response.sendError(403);
  }
}
