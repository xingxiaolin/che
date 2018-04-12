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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.che.dto.server.DtoFactory.newDto;
import static org.eclipse.che.multiuser.keycloak.shared.KeycloakConstants.AUTH_SERVER_URL_SETTING;
import static org.eclipse.che.multiuser.keycloak.shared.KeycloakConstants.REALM_SETTING;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.jayway.restassured.RestAssured;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.multiuser.keycloak.shared.dto.KeycloakErrorResponse;
import org.eclipse.che.multiuser.keycloak.shared.dto.KeycloakTokenResponse;
import org.everrest.assured.EverrestJetty;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** @author Max Shaposhnik (mshaposh@redhat.com) */
@Listeners(value = {EverrestJetty.class, MockitoTestNGListener.class})
public class KeycloakServiceClientTest {

  @Mock private KeycloakSettings keycloakSettings;

  private KeycloakServiceClient keycloakServiceClient;

  @SuppressWarnings("unused")
  private KeycloakService keycloakService;

  @SuppressWarnings("unused")
  private final LocalApiExceptionMapper exceptionMapper = new LocalApiExceptionMapper();

  @BeforeMethod
  public void setUp() throws Exception {
    keycloakServiceClient = new KeycloakServiceClient(keycloakSettings);
    Map<String, String> conf = new HashMap<>();
    conf.put(
        AUTH_SERVER_URL_SETTING,
        RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath);
    conf.put(REALM_SETTING, "che");
    when(keycloakSettings.get()).thenReturn(conf);
  }

  @Test
  public void shouldReturnToken() throws Exception {
    String token = "token123";
    String scope = "test_scope";
    String tokenType = "test_type";
    keycloakService = new KeycloakService(token, scope, tokenType, null);
    KeycloakTokenResponse response = keycloakServiceClient.getIdentityProviderToken("github");
    assertNotNull(response);
    assertEquals(response.getAccessToken(), token);
    assertEquals(response.getScope(), scope);
    assertEquals(response.getTokenType(), tokenType);
  }

  @Test(
    expectedExceptions = BadRequestException.class,
    expectedExceptionsMessageRegExp = "Invalid token."
  )
  public void shouldThrowBadRequestException() throws Exception {
    keycloakService =
        new KeycloakService(null, null, null, new BadRequestException("Invalid token."));
    keycloakServiceClient.getIdentityProviderToken("github");
  }

  @Test(
    expectedExceptions = ForbiddenException.class,
    expectedExceptionsMessageRegExp = "Forbidden."
  )
  public void shouldThrowForbiddenException() throws Exception {
    keycloakService = new KeycloakService(null, null, null, new ForbiddenException("Forbidden."));
    keycloakServiceClient.getIdentityProviderToken("github");
  }

  @Test(
    expectedExceptions = UnauthorizedException.class,
    expectedExceptionsMessageRegExp = "Unauthorized."
  )
  public void shouldThrowUnauthorizedException() throws Exception {
    keycloakService =
        new KeycloakService(null, null, null, new UnauthorizedException("Unauthorized."));
    keycloakServiceClient.getIdentityProviderToken("github");
  }

  @Test(
    expectedExceptions = NotFoundException.class,
    expectedExceptionsMessageRegExp = "Not found."
  )
  public void shouldThrowNotFoundException() throws Exception {
    keycloakService = new KeycloakService(null, null, null, new NotFoundException("Not found."));
    keycloakServiceClient.getIdentityProviderToken("github");
  }

  // Special case
  @Test(
    expectedExceptions = UnauthorizedException.class,
    expectedExceptionsMessageRegExp = "User (.+) is not associated with identity provider (.+)."
  )
  public void shouldThrowUnauthorizedExceptionWhenNoProviderLink() throws Exception {
    keycloakService =
        new KeycloakService(
            null,
            null,
            null,
            new BadRequestException(
                "User 1234-5678-90 is not associated with identity provider gitlab."));
    keycloakServiceClient.getIdentityProviderToken("github");
  }

  @Path("/realms/che")
  public class KeycloakService extends Service {

    private String token;
    private String scope;
    private String tokenType;
    private ApiException exception;

    public KeycloakService(String token, String scope, String tokenType, ApiException exception) {
      this.token = token;
      this.scope = scope;
      this.tokenType = tokenType;
      this.exception = exception;
    }

    @GET
    @Path("/broker/{provider}/token")
    @Produces(APPLICATION_JSON)
    public String getToken(@PathParam("provider") String provider) throws Exception {
      if (exception == null) {
        return "access_token=" + token + "&scope=" + scope + "&tokenType=" + tokenType;
      } else {
        throw exception;
      }
    }
  }

  @Provider
  public class LocalApiExceptionMapper implements ExceptionMapper<ApiException> {
    @Override
    public Response toResponse(ApiException exception) {

      if (exception instanceof ForbiddenException)
        return Response.status(Response.Status.FORBIDDEN)
            .entity(
                DtoFactory.getInstance()
                    .toJson(
                        newDto(KeycloakErrorResponse.class)
                            .withErrorMessage(exception.getServiceError().getMessage())))
            .type(MediaType.APPLICATION_JSON)
            .build();
      else if (exception instanceof NotFoundException)
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                DtoFactory.getInstance()
                    .toJson(
                        newDto(KeycloakErrorResponse.class)
                            .withErrorMessage(exception.getServiceError().getMessage())))
            .type(MediaType.APPLICATION_JSON)
            .build();
      else if (exception instanceof UnauthorizedException)
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(
                DtoFactory.getInstance()
                    .toJson(
                        newDto(KeycloakErrorResponse.class)
                            .withErrorMessage(exception.getServiceError().getMessage())))
            .type(MediaType.APPLICATION_JSON)
            .build();
      else if (exception instanceof BadRequestException)
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(
                DtoFactory.getInstance()
                    .toJson(
                        newDto(KeycloakErrorResponse.class)
                            .withErrorMessage(exception.getServiceError().getMessage())))
            .type(MediaType.APPLICATION_JSON)
            .build();
      else if (exception instanceof ServerException)
        return Response.serverError()
            .entity(
                DtoFactory.getInstance()
                    .toJson(
                        newDto(KeycloakErrorResponse.class)
                            .withErrorMessage(exception.getServiceError().getMessage())))
            .type(MediaType.APPLICATION_JSON)
            .build();
      else
        return Response.serverError()
            .entity(
                DtoFactory.getInstance()
                    .toJson(
                        newDto(KeycloakErrorResponse.class)
                            .withErrorMessage(exception.getServiceError().getMessage())))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
  }
}
