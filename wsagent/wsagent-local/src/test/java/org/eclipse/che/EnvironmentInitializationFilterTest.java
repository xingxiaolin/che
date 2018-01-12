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
package org.eclipse.che;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotEquals;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.subject.SubjectImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** @author Max Shaposhnik (mshaposh@redhat.com) */
@Listeners(value = {MockitoTestNGListener.class})
public class EnvironmentInitializationFilterTest {

  @Mock private FilterChain chain;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @InjectMocks private EnvironmentInitializationFilter filter;

  @Test
  public void shouldSkipRequestToProject() throws Exception {
    // given
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/ws/ws-id");

    EnvironmentContext context = spy(EnvironmentContext.getCurrent());
    EnvironmentContext.setCurrent(context);

    // when
    filter.doFilter(request, response, chain);

    // then
    verify(chain).doFilter(eq(request), eq(response));
    verify(context).setSubject(eq(new SubjectImpl("che", "che", "dummy_token", false)));
    // after reset
    assertNotEquals(EnvironmentContext.getCurrent(), context);
  }
}
