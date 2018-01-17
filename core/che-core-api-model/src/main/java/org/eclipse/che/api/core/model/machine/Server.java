/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.core.model.machine;

import org.eclipse.che.commons.annotation.Nullable;

/**提供机器中的服务器的描述。
 * Provides description of the Che server in machine
 *
 * @author gazarenkov
 * @author Alexander Garagatyi
 */
public interface Server {
    /**
     * Reference to this Che server
     */
    String getRef();

    /**
     * External address of the server in form <b>hostname:port</b>.
     * <p>
     * This address is used by the browser to communicate with the server.
     * <b>port</b> is the external port and cannot be configured.
     * If not explicitly configured that address is set using {@link ServerProperties#getInternalAddress()}
     */
    String getAddress();

    /**
     * Protocol of access to the server.访问服务器协议。
     */
    @Nullable
    String getProtocol();

    /**URL的服务器，例如 http://localhost:8080
     * Url of the server, e.g.&nbsp;http://localhost:8080
     */
    @Nullable
    String getUrl();


    /**
     * Non mandatory properties of the server.服务器的非强制属性。
     */
    @Nullable
    ServerProperties getProperties();
}
