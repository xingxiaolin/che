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
package org.eclipse.che.workspace.infrastructure.kubernetes.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides single {@link ExecutorService} instance with daemon threads for Kubernetes/Openshfit
 * infrastructures components.
 *
 * @author Anton Korneta
 */
@Singleton
public class KubernetesSharedPool {

  private final ExecutorService executor;

  @Inject
  public KubernetesSharedPool() {
    final ThreadFactory factory =
        new ThreadFactoryBuilder()
            .setNameFormat("KubernetesMachineSharedPool-%d")
            .setDaemon(true)
            .build();
    this.executor = Executors.newCachedThreadPool(factory);
  }

  public ExecutorService getExecutor() {
    return executor;
  }
}
