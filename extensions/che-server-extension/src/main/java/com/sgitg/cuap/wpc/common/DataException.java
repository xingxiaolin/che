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
package com.sgitg.cuap.wpc.common;

import com.sgitg.cuap.wpc.common.InvocationException;

/**
 * 服务端框架通用异常
 *
 */
public class DataException extends InvocationException {
	private static final long serialVersionUID = 3312524754272560860L;


	public DataException(String message) {
		super(message);
	}

	public DataException() {
		super();
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(Throwable cause) {
		super(cause);
	}
}