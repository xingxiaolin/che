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
package org.eclipse.che.multiuser.api.permission.server;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class PermissionCheckerImplTest {
  @Mock private PermissionsManager permissionsManager;

  @InjectMocks private PermissionCheckerImpl permissionChecker;

  @Test
  public void shouldCheckExistingDirectUsersPermissions() throws Exception {
    when(permissionsManager.exists(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(true);

    boolean hasPermission =
        permissionChecker.hasPermission("user123", "domain123", "instance123", "test");

    assertEquals(hasPermission, true);
    verify(permissionsManager).exists("user123", "domain123", "instance123", "test");
  }

  @Test
  public void shouldCheckExistingPublicPermissionsIfThereIsNoDirectUsersPermissions()
      throws Exception {
    when(permissionsManager.exists(eq("user123"), anyString(), anyString(), anyString()))
        .thenReturn(false);
    when(permissionsManager.exists(eq("*"), anyString(), anyString(), anyString()))
        .thenReturn(true);

    boolean hasPermission =
        permissionChecker.hasPermission("user123", "domain123", "instance123", "test");

    assertEquals(hasPermission, true);
    verify(permissionsManager).exists("user123", "domain123", "instance123", "test");
    verify(permissionsManager).exists("*", "domain123", "instance123", "test");
  }
}
