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
package org.eclipse.che.ide.ext.java.client.search;

import static org.eclipse.che.ide.MimeType.APPLICATION_JSON;
import static org.eclipse.che.ide.rest.HTTPHeader.CONTENT_TYPE;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.ext.java.shared.dto.search.FindUsagesRequest;
import org.eclipse.che.ide.ext.java.shared.dto.search.FindUsagesResponse;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;
import org.eclipse.che.ide.ui.loaders.request.MessageLoader;

/**
 * Default implementation for <code>JavaSearchService</code>
 *
 * @author Evgen Vidolob
 */
@Singleton
public class JavaSearchServiceRest implements JavaSearchService {

  private final AsyncRequestFactory asyncRequestFactory;
  private final DtoUnmarshallerFactory unmarshallerFactory;
  private MessageLoader loader;
  private final String pathToService;

  @Inject
  public JavaSearchServiceRest(
      AsyncRequestFactory asyncRequestFactory,
      DtoUnmarshallerFactory unmarshallerFactory,
      LoaderFactory loaderFactory,
      AppContext appContext) {
    this.asyncRequestFactory = asyncRequestFactory;
    this.unmarshallerFactory = unmarshallerFactory;
    this.loader = loaderFactory.newLoader();
    this.pathToService = appContext.getWsAgentServerApiEndpoint() + "/jdt/search/";
  }

  @Override
  public Promise<FindUsagesResponse> findUsages(final FindUsagesRequest request) {
    return asyncRequestFactory
        .createPostRequest(pathToService + "find/usages", request)
        .header(CONTENT_TYPE, APPLICATION_JSON)
        .loader(loader)
        .send(unmarshallerFactory.newUnmarshaller(FindUsagesResponse.class));
  }
}
