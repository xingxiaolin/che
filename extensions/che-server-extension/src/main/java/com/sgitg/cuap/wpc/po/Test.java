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
package com.sgitg.cuap.wpc.po;

public class Test {
	private String id = null;
	private String name = null;
	private String title = null;
	private String fullName = null;
	private String service = null;
	private String script = null;
	private String scriptClass = null;
	private String css = null;
	private String bundleId = null;
	private String type = null;
	private String data = null;
	private String dataId = null;
	/** 表单标注 */
	private String remark = null;
	
	private String path;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
			this.path =path;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDataId() {
		return dataId;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getService() {
		return service;
	}
	public void setService(String srevice) {
		this.service = srevice;
	}
	public String getBundleId() {
		return bundleId;
	}
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getData() {
		return data;
	}
	public void setScriptClass(String scriptClass) {
		this.scriptClass = scriptClass;
	}
	public String getScriptClass() {
		return scriptClass;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
//	public byte[] getDataBytes() {
//		if(formatData == null && data != null){
//			synchronized(this){
//				try {
//					if(formatData == null && data != null){
//						formatData = data.getBytes("UTF-8");
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return formatData;
//	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}