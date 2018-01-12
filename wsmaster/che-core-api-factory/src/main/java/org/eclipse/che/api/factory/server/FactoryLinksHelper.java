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
package org.eclipse.che.api.factory.server;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.eclipse.che.api.core.util.LinksHelper.createLink;
import static org.eclipse.che.api.factory.shared.Constants.FACTORY_ACCEPTANCE_REL_ATT;
import static org.eclipse.che.api.factory.shared.Constants.NAMED_FACTORY_ACCEPTANCE_REL_ATT;
import static org.eclipse.che.api.factory.shared.Constants.RETRIEVE_FACTORY_REL_ATT;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.che.api.core.rest.ServiceContext;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.factory.shared.dto.FactoryDto;

/**
 * Helper class for creation links.
 *
 * @author Anton Korneta
 */
public class FactoryLinksHelper {

  private static final List<String> SNIPPET_TYPES =
      ImmutableList.of("markdown", "url", "html", "iframe");

  private FactoryLinksHelper() {}

  /**
   * Creates factory links.
   *
   * @param serviceContext the context to retrieve factory service base URI
   * @return list of factory links
   */
  public static List<Link> createLinks(
      FactoryDto factory, ServiceContext serviceContext, String userName) {
    final List<Link> links = new LinkedList<>();
    final UriBuilder uriBuilder = serviceContext.getServiceUriBuilder();
    final String factoryId = factory.getId();
    if (factoryId != null) {
      // creation of link to retrieve factory
      links.add(
          createLink(
              HttpMethod.GET,
              uriBuilder
                  .clone()
                  .path(FactoryService.class, "getFactory")
                  .build(factoryId)
                  .toString(),
              null,
              APPLICATION_JSON,
              RETRIEVE_FACTORY_REL_ATT));
      // creation of accept factory link
      final Link createWorkspace =
          createLink(
              HttpMethod.GET,
              uriBuilder.clone().replacePath("f").queryParam("id", factoryId).build().toString(),
              null,
              TEXT_HTML,
              FACTORY_ACCEPTANCE_REL_ATT);
      links.add(createWorkspace);
    }

    if (!Strings.isNullOrEmpty(factory.getName()) && !Strings.isNullOrEmpty(userName)) {
      // creation of accept factory link by name and creator
      final Link createWorkspaceFromNamedFactory =
          createLink(
              HttpMethod.GET,
              uriBuilder
                  .clone()
                  .replacePath("f")
                  .queryParam("name", factory.getName())
                  .queryParam("user", userName)
                  .build()
                  .toString(),
              null,
              TEXT_HTML,
              NAMED_FACTORY_ACCEPTANCE_REL_ATT);
      links.add(createWorkspaceFromNamedFactory);
    }
    return links;
  }
}
