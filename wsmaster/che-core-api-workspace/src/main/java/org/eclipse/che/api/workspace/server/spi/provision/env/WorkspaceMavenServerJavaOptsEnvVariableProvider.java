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
package org.eclipse.che.api.workspace.server.spi.provision.env;

import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.commons.lang.Pair;

public class WorkspaceMavenServerJavaOptsEnvVariableProvider implements EnvVarProvider {

  /** Env variable for jvm settings */
  public static final String CHE_WORKSPACE_MAVEN_SERVER_JAVA_OPTIONS =
      "CHE_WORKSPACE_MAVEN__SERVER__JAVA__OPTIONS";

  private String javaOpts;

  @Inject
  public WorkspaceMavenServerJavaOptsEnvVariableProvider(
      @Named("che.workspace.maven_server_java_options") String javaOpts,
      @Nullable @Named("che.workspace.http_proxy_java_options") String httpProxyJavaOptions) {
    this.javaOpts = httpProxyJavaOptions == null ? javaOpts : javaOpts + " " + httpProxyJavaOptions;
  }

  @Override
  public Pair<String, String> get(RuntimeIdentity runtimeIdentity) {
    return Pair.of(CHE_WORKSPACE_MAVEN_SERVER_JAVA_OPTIONS, javaOpts);
  }
}
