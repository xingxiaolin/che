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
package org.eclipse.che.multiuser.api.permission.server.jpa;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.eclipse.che.multiuser.api.permission.server.AbstractPermissionsDomain;
import org.eclipse.che.multiuser.api.permission.server.SystemDomain;
import org.eclipse.che.multiuser.api.permission.server.model.impl.AbstractPermissions;
import org.eclipse.che.multiuser.api.permission.server.model.impl.SystemPermissionsImpl;
import org.eclipse.che.multiuser.api.permission.server.spi.PermissionsDao;

/** @author Max Shaposhnik */
public class SystemPermissionsJpaModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(new TypeLiteral<AbstractPermissionsDomain<SystemPermissionsImpl>>() {})
        .to(SystemDomain.class);
    bind(JpaSystemPermissionsDao.RemoveSystemPermissionsBeforeUserRemovedEventSubscriber.class)
        .asEagerSingleton();

    Multibinder<PermissionsDao<? extends AbstractPermissions>> storages =
        Multibinder.newSetBinder(
            binder(), new TypeLiteral<PermissionsDao<? extends AbstractPermissions>>() {});
    storages.addBinding().to(JpaSystemPermissionsDao.class);
  }
}
