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

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class InvocationException extends Exception {
	private static final long serialVersionUID = -4011960445850854413L;
	/**
	 * 当值为true时，清除信息中的数据库相关内容
	 * 外部程序可设置此参数，默认总是为false
	 */
	public static boolean hideDBInfo = false;

	protected String errorCode = null;
	/**
	 * 嵌套异常信息，因为本异常类用于服务器端向客户端传递，
	 * 所以这里只提取嵌套异常的堆栈信息
	 */
	protected CauseInfo causeInfo = null;
	/**
	 * InvocationException类的构造方法
	 *
	 */
	public InvocationException(){
		super();
	}
	/**
	 * InvocationException类的构造方法
	 * @param Throwable e
	 */
	public InvocationException(Throwable e){
		this(null,e);
	}
	/**
	 * InvocationException类的构造方法
	 * Throwable
	 * @param String decription
	 */
	public InvocationException(String description){
		this(null, description, null);
	}
	/**
	 * InvocationException类的构造方法
	 * @param String decription
	 * @param Throwable e
	 */
	public InvocationException(String description,Throwable e){
		this(null, description, e);
	}
	/**
	 * InvocationException类的构造方法
	 * @param String code
	 * @param String decription
	 */

	public InvocationException(String code,String description){
		this(code, description, null);
	}
	/**
	 * InvocationException类的构造方法
	 * @param String code
	 * @param String decription
	 * @param Throwable e
	 */
	public InvocationException(String code,String description,Throwable e){
		super(hideDBInfo(description == null && e != null ? e.getMessage() : description));
		if(e != null)causeInfo = new CauseInfo(e);
		errorCode = code;
	}
	/**
	 * 隐藏数据库相关信息
	 * @param info
	 * @return
	 */
	private static String hideDBInfo(String info){
		if(info == null || !hideDBInfo)return info;
		int k = info.indexOf("ORA-");
		if(k > 0){
			info = info.substring(0, k) + info.substring(k + 10);
			k = info.indexOf("(");
			if(k > 0){
				info = info.substring(0, k);
			}
		}
		return info;
	}
	/**
	 * 获取错误编码
	 * @return String
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * 获取嵌套异常信息
	 * @return
	 */
	public CauseInfo getCauseInfo(){
		return causeInfo;
	}

	public void printStackTrace(){
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream ps){
		super.printStackTrace(ps);
		if(causeInfo != null){
			causeInfo.printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw){
		super.printStackTrace(pw);
		if(causeInfo != null){
			causeInfo.printStackTrace(pw);
		}
	}

	public static class CauseInfo implements Serializable{
		/**
		 *
		 */
		private static final long serialVersionUID = -1170712722072773490L;
		private String message = null;
		private String stackTrace = null;
		private CauseInfo cause = null;

		public CauseInfo(Throwable e){
			message = e.getMessage();

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			pw.close();
			try {
				stackTrace = sw.toString();
				sw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			/*
			 * JDK 1.4以上支持
			 */
//			if(e.getCause() != null){
//				causeInfo = new ThrowableInfo(e.getCause());
//			}
		}

		public String getMessage(){
			return message;
		}
		public String getStackTrace(){
			return stackTrace;
		}
		public CauseInfo getCauseInfo(){
			return cause;
		}
		public void printStackTrace(PrintStream ps){
			ps.println("Caused by : " + getMessage());
			if(getStackTrace() != null){
				ps.println(getStackTrace());
			}
			if(getCauseInfo() != null){
				getCauseInfo().printStackTrace(ps);
			}
		}
		public void printStackTrace(PrintWriter pw){
			pw.println("Caused by : " + getMessage());
			if(getStackTrace() != null){
				pw.println(getStackTrace());
			}
			if(getCauseInfo() != null){
				getCauseInfo().printStackTrace(pw);
			}
		}
	}
}