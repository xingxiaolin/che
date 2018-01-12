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
package org.eclipse.che.multiuser.resource.api.usage;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.rest.Service;
import org.eclipse.che.multiuser.resource.api.DtoConverter;
import org.eclipse.che.multiuser.resource.shared.dto.ResourceDto;

/**
 * Defines Resource REST API.
 *
 * @author Sergii Leschenko
 */
@Api(value = "/resource", description = "Resource REST API")
@Path("/resource")
public class ResourceUsageService extends Service {
  private final ResourceUsageManager resourceUsageManager;

  @Inject
  public ResourceUsageService(ResourceUsageManager resourceUsageManager) {
    this.resourceUsageManager = resourceUsageManager;
  }

  @GET
  @Path("/{accountId}")
  @Produces(APPLICATION_JSON)
  @ApiOperation(
    value = "Get list of resources which are available for given account",
    response = ResourceDto.class,
    responseContainer = "List"
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "The total resources are successfully fetched"),
    @ApiResponse(code = 404, message = "Account with specified id was not found"),
    @ApiResponse(code = 500, message = "Internal server error occurred")
  })
  public List<ResourceDto> getTotalResources(
      @ApiParam("Account id") @PathParam("accountId") String accountId)
      throws NotFoundException, ServerException, ConflictException {
    return resourceUsageManager
        .getTotalResources(accountId)
        .stream()
        .map(DtoConverter::asDto)
        .collect(Collectors.toList());
  }

  @GET
  @Path("/{accountId}/available")
  @Produces(APPLICATION_JSON)
  @ApiOperation(
    value = "Get list of resources which are available for usage by given account",
    response = ResourceDto.class,
    responseContainer = "List"
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "The available resources are successfully fetched"),
    @ApiResponse(code = 404, message = "Account with specified id was not found"),
    @ApiResponse(code = 500, message = "Internal server error occurred")
  })
  public List<ResourceDto> getAvailableResources(@PathParam("accountId") String accountId)
      throws NotFoundException, ServerException {
    return resourceUsageManager
        .getAvailableResources(accountId)
        .stream()
        .map(DtoConverter::asDto)
        .collect(Collectors.toList());
  }

  @GET
  @Path("/{accountId}/used")
  @Produces(APPLICATION_JSON)
  @ApiOperation(
    value = "Get list of resources which are used by given account",
    response = ResourceDto.class,
    responseContainer = "List"
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "The used resources are successfully fetched"),
    @ApiResponse(code = 404, message = "Account with specified id was not found"),
    @ApiResponse(code = 500, message = "Internal server error occurred")
  })
  public List<ResourceDto> getUsedResources(@PathParam("accountId") String accountId)
      throws NotFoundException, ServerException {
    return resourceUsageManager
        .getUsedResources(accountId)
        .stream()
        .map(DtoConverter::asDto)
        .collect(Collectors.toList());
  }
}
