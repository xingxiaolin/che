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

/**
 * Defines runtime machine.定义运行时机器。
 *
 * @author gazarenkov
 * @author Alexander Garagatyi
 */
public interface Machine {
    /**
     * Returns configuration used to create this machine 返回用于创建此机器的配置。
     */
    MachineConfig getConfig();

    /**
     * Returns machine identifier. It is unique and mandatory. 返回机器标识符。它是独一无二的，也是强制性的。
     */
    String getId();

    /**
     * Returns ID of workspace this machine belongs to. It is mandatory.返回此机器属于的工作区ID。这是强制性的。
     */
    String getWorkspaceId();

    /**
     * Returns name of environment that started this machine. It is mandatory.返回启动此机器的环境名称。这是强制性的。
     */
    String getEnvName();

    /**
     * Returns machine owner (users identifier). It is mandatory.返回机器所有者（用户标识符）。这是强制性的。
     */
    String getOwner();

    /**
     * Runtime status of the machine 机器的运行状态
     */
    MachineStatus getStatus();

    /**
     * Runtime information about machine.关于机器的运行时信息。
     * <p>
     * Is available only when {@link #getStatus()} returns {@link MachineStatus#RUNNING}
     */
    @Nullable
    MachineRuntimeInfo getRuntime();
}
