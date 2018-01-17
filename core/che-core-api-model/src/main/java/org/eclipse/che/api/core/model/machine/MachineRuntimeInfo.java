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

import java.util.Map;

/**
 * Runtime information about machine.关于机器的运行时信息。
 *
 * @author Alexander Garagatyi
 */
public interface MachineRuntimeInfo {
    /**
     * Returns environment variables of machine.返回机器的环境变量。
     */
    Map<String, String> getEnvVariables();

    /**
     * Returns machine specific properties.返回特定于机器的属性。
     */
    Map<String, String> getProperties();

    /**
     * It is supposed that this methods returns the same as {@code getEnvVariables().get("CHE_PROJECTS_ROOT")}.
     */
    String projectsRoot();

    /**将暴露端口映射到{链接服务器}。
     * Returns mapping of exposed ports to {@link Server}.
     *键由端口号和传输协议组成，TCP或UDP在这些部分之间有斜线。
     * <p>Key consist of port number and transport protocol - tcp or udp with slash between these parts.键由端口号和传输协议组成，TCP或UDP在这些部分之间有斜线。
     * <br>Example:
     * <pre>
     * {
     *     8080/tcp : {
     *         "ref" : "server_reference",
     *         "address" : "server-with-machines.com",
     *         "url" : "http://server-with-machines.com:8080"
     *     }
     * }
     * </pre>
     */
    Map<String, ? extends Server> getServers();
    //"4401/tcp":{
		//"ref":"wsagent",
		//"protocol":"http",
		//"address":"192.168.148.130:32878",
		//"url":"http://192.168.148.130:32878/api",
		//"properties":{"path":"/api","internalAddress":"192.168.148.130:32878","internalUrl":"http://192.168.148.130:32878/api"}
	//},
}
