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
package com.sgitg.cuap.wpc.designer.common.biz.impl;

import java.io.File;
import com.sgitg.cuap.wpc.codejet.scanner.IRootLocator;

public class SpaceRootLocator implements IRootLocator{
	 //空间id
		private String spaceId;
		private String hostInfo;
		
		public SpaceRootLocator(String spaceId,String hostInfo){
			this.spaceId = spaceId;
			this.hostInfo = hostInfo;
			
		}
		
		/**
		 * 获取磁盘根路径：eg：C:\cuapOriCode
		 * @return
		 */
		public String getCuapPath() {
			//public static final String CUAP_XML_BASE_PATH = Config.getProperty("wpc_root")+ File.separator + "cuapOriCode";
			//String rootPath =RcpConstants.CUAP_XML_BASE_PATH;
			return File.separator +"home" + File.separator +"user" + File.separator ;
		}
		
		/**
		 * 获取磁盘根路径：eg：.../spaceId/Projects
		 * @return
		 */
		public String getRootPath() {
			String rootPath = File.separator  + "projects";
			
			return rootPath;
		}
		/**
		 * 获取翻译（转mx）后的磁盘根路径:eg：.../spaceId/Source
		 * @return
		 */
		public String getTargetPath() {
			//String targetPath =  RcpConstants.CUAP_XML_BASE_PATH + File.separator + spaceId + File.separator + "Sources";
			String targetPath = this.getCuapPath() + "Sources";
			return targetPath;
		}
		
		/**
		 * 获取转码（osgi/微服务）源码压缩包存储路径:eg：.../zip
		 * @return
		 */
		public String getTargetZipPath(){
			String genZipRootPath = this.getCuapPath() + "zip";
			return genZipRootPath;
		}
		
		/**
		 * 获取服务（根据数据模型名称标识 获取对应的xml信息）url；:eg："http://192.168.1.1:9000/cuap/dmm-datamodel/rest/define/getModelXMLInfo"
		 * @return
		 */
		public String getModelXMLUrl() {
			String convertRootPath =  this.hostInfo + "/cuap/dmm-datamodel/rest/define/getModelXMLInfo";
			return convertRootPath;
		}
}
