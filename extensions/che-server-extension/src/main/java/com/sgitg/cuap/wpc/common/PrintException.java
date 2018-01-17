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

public class PrintException {
	public static void getErrorMessage(Exception e){
		System.out.println("------------------------------------进入异常日志打印------------------------------------");
		String msg = e.getMessage();
		System.out.println("错误消息：["+msg + "]");
		StackTraceElement[] st = e.getStackTrace();
		for (StackTraceElement stackTraceElement : st) {
			String exclass = stackTraceElement.getClassName();
			String method = stackTraceElement.getMethodName();
			int lineNum = stackTraceElement.getLineNumber();
			System.out.println("异常:类路径：["+exclass+"]；方法：["+method + "]；第[" + lineNum + "]行！");
		}
	}
}
