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
package org.eclipse.che.selenium.core.workspace;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import java.lang.reflect.Field;
import org.eclipse.che.selenium.core.user.TestUser;

/**
 * Injector for custom annotation {@link InjectTestWorkspace}.
 *
 * @author Anatolii Bazko
 */
public class TestWorkspaceInjector<T> implements MembersInjector<T> {
  private final Field field;
  private final InjectTestWorkspace injectTestWorkspace;
  private final Provider<Injector> injectorProvider;

  public TestWorkspaceInjector(
      Field field, InjectTestWorkspace injectTestWorkspace, Provider<Injector> injectorProvider) {
    this.field = field;
    this.injectTestWorkspace = injectTestWorkspace;
    this.injectorProvider = injectorProvider;
  }

  @Override
  public void injectMembers(T instance) {
    Injector injector = injectorProvider.get();

    TestWorkspaceProvider testWorkspaceProvider = injector.getInstance(TestWorkspaceProvider.class);
    int workspaceDefaultMemoryGb =
        injector.getInstance(Key.get(int.class, Names.named("workspace.default_memory_gb")));

    try {
      TestUser testUser =
          isNullOrEmpty(injectTestWorkspace.user())
              ? injector.getInstance(TestUser.class)
              : findInjectedUser(instance, injectTestWorkspace.user());

      int memoryGb =
          injectTestWorkspace.memoryGb() <= 0
              ? workspaceDefaultMemoryGb
              : injectTestWorkspace.memoryGb();

      TestWorkspace testWorkspace =
          testWorkspaceProvider.createWorkspace(testUser, memoryGb, injectTestWorkspace.template());
      testWorkspace.await();

      field.setAccessible(true);
      field.set(instance, testWorkspace);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to instantiate workspace in " + instance.getClass().getName(), e);
    }
  }

  private TestUser findInjectedUser(T instance, String userEmail) {
    for (Field field : instance.getClass().getDeclaredFields()) {
      if (field.getType() == TestUser.class) {
        field.setAccessible(true);

        TestUser testUser;
        try {
          testUser = (TestUser) field.get(instance);
        } catch (IllegalAccessException e) {
          throw new IllegalArgumentException(e);
        }

        if (testUser != null && testUser.getEmail().equals(userEmail)) {
          return testUser;
        }
      }
    }

    throw new IllegalArgumentException(
        format(
            "User %s not found or isn't instantiated in %s",
            userEmail, instance.getClass().getName()));
  }
}
