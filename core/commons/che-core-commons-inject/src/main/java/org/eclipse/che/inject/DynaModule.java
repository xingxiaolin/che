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
package org.eclipse.che.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker annotation for dynamically created modules.
 *
 * <p>{@link CheBootstrap} automatically finds and loads Guice modules (subclasses of {@link
 * com.google.inject.Module}) annotated with &#064DynaModule.
 *
 * @author gazarenkov
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DynaModule {}
