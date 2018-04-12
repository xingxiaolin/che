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
package org.eclipse.che.multiuser.machine.authentication.server;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.eclipse.che.api.workspace.server.spi.provision.env.EnvVarProvider;
import org.eclipse.che.api.workspace.server.token.MachineTokenProvider;
import org.eclipse.che.multiuser.machine.authentication.server.signature.SignatureAlgorithmEnvProvider;
import org.eclipse.che.multiuser.machine.authentication.server.signature.SignatureKeyManager;
import org.eclipse.che.multiuser.machine.authentication.server.signature.SignaturePublicKeyEnvProvider;
import org.eclipse.che.multiuser.machine.authentication.server.signature.jpa.JpaSignatureKeyDao;
import org.eclipse.che.multiuser.machine.authentication.server.signature.spi.SignatureKeyDao;

/**
 * Machine auth module.
 *
 * @author Max Shaposhnik
 * @author Sergii Leshchenko
 */
public class MachineAuthModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(MachineSessionInvalidator.class).asEagerSingleton();

    bind(MachineTokenProvider.class).to(MachineTokenProviderImpl.class);

    bind(SignatureKeyManager.class);
    bind(SignatureKeyDao.class).to(JpaSignatureKeyDao.class);
    final Multibinder<EnvVarProvider> envVarProviders =
        Multibinder.newSetBinder(binder(), EnvVarProvider.class);
    envVarProviders.addBinding().to(SignaturePublicKeyEnvProvider.class);
    envVarProviders.addBinding().to(SignatureAlgorithmEnvProvider.class);
    bindConstant().annotatedWith(Names.named("che.auth.signature_key_size")).to(2048);
    bindConstant().annotatedWith(Names.named("che.auth.signature_key_algorithm")).to("RSA");
  }
}
