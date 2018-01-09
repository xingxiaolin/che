/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.commons.logback.filter;

import java.io.IOException;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

/**
 * A servlet filter that retrieves the X-Request-Id header from the http request and put it in the
 * MDC context. Logback can be configured to display this value in each log message when available.
 * MDC property name is `req_id`.
 */
@Singleton
public class RequestIdLoggerFilter implements Filter {

  private static final String REQUEST_ID_HEADER = "X-Request-Id";
  private static final String REQUEST_ID_MDC_KEY = "req_id";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public final void doFilter(
      ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
    if (requestId != null) {
      MDC.put(REQUEST_ID_MDC_KEY, requestId);
    }

    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
}
