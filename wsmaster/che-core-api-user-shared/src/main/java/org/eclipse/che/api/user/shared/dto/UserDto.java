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
package org.eclipse.che.api.user.shared.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.eclipse.che.api.core.model.user.User;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.dto.shared.DTO;

/**
 * This object used for transporting user data to/from client.
 *
 * @author Yevhenii Voevodin
 * @see User
 * @see DtoFactory
 */
@DTO
public interface UserDto extends User {
  @ApiModelProperty("User ID")
  String getId();

  void setId(String id);

  UserDto withId(String id);

  @ApiModelProperty("User alias which is used for oAuth")
  List<String> getAliases();

  void setAliases(List<String> aliases);

  UserDto withAliases(List<String> aliases);

  @ApiModelProperty("User email")
  String getEmail();

  void setEmail(String email);

  UserDto withEmail(String email);

  @ApiModelProperty("User name")
  String getName();

  void setName(String name);

  UserDto withName(String name);

  @ApiModelProperty("User password")
  String getPassword();

  void setPassword(String password);

  UserDto withPassword(String password);

  List<Link> getLinks();

  void setLinks(List<Link> links);

  UserDto withLinks(List<Link> links);
}
