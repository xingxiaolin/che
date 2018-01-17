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
package com.sgitg.cuap.wpc.old;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.File;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sgitg.cuap.wpc.common.XMLHelper;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.inject.Inject;
import com.sgitg.cuap.pojo.VFormInfo;

@Path("/saveFormFileService")
public class SaveFormFileService{
	
	@Inject
	public SaveFormFileService(){
	}	
	
	@Path("/saveFormFile/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public boolean saveFormFile(VFormInfo formInfo){
		System.out.println("999999999999999999");
    	boolean flag = true;    	
		String name = formInfo.getName();
		if( null == name ||"".equals(name)){
			name = formInfo.getTitle()== null ? "":formInfo.getTitle();
		}
		try {
			Document doc = XMLHelper.createDocument("FORM");
			Element formInfoEle = doc.getDocumentElement();
			formInfoEle.setAttribute("ID", formInfo.getId());

			Element eleId = doc.createElement("ID");
			eleId.appendChild(doc.createTextNode(formInfo.getId()));
			formInfoEle.appendChild(eleId);
			
			Element eleDataId = doc.createElement("DATA_ID");
			eleDataId.appendChild(doc.createTextNode(formInfo.getDataId()));
			formInfoEle.appendChild(eleDataId);

			Element eleTitle = doc.createElement("TITLE");
			eleTitle.appendChild(doc.createTextNode(""+ formInfo.getTitle()));
			formInfoEle.appendChild(eleTitle);
			
			Element eleScript = doc.createElement("SCRIPT");
			eleScript.appendChild(doc.createTextNode("" + formInfo.getScript()));
			formInfoEle.appendChild(eleScript);
			
			Element eleCss = doc.createElement("CSS");
			eleCss.appendChild(doc.createTextNode("" + formInfo.getCss()));
			formInfoEle.appendChild(eleCss);
			
			Element eleFullName = doc.createElement("FULL_NAME");
			eleFullName.appendChild(doc.createTextNode("" + formInfo.getFullName()));
			formInfoEle.appendChild(eleFullName);
			
			Element eleScriptC = doc.createElement("SCRIPT_CLASS");
			eleScriptC.appendChild(doc.createTextNode("" + formInfo.getScriptClass()));
			formInfoEle.appendChild(eleScriptC);
			
			Element eleName = doc.createElement("NAME");
			eleName.appendChild(doc.createTextNode(name));
			formInfoEle.appendChild(eleName);

			Element eleService = doc.createElement("SERVICE");
			eleService.appendChild(doc.createTextNode("" + formInfo.getService()));
			formInfoEle.appendChild(eleService);
			
			Element eleBundleId = doc.createElement("BUNDLE_ID");
			eleBundleId.appendChild(doc.createTextNode("" + formInfo.getBundleId()));
			formInfoEle.appendChild(eleBundleId);
			
			Element eleData = doc.createElement("DATA");
			CDATASection cdata = doc.createCDATASection("" + formInfo.getData());//将源码信息保存为CDATA
			eleData.appendChild(cdata);
			formInfoEle.appendChild(eleData);
			
//			Element eleRemark = doc.createElement("REMARK");
//			eleRemark.appendChild(doc.createTextNode("" + formInfo.getRemark()));
//			formInfoEle.appendChild(eleRemark);
			//保存
			String str = File.separator+"projects"+ File.separator+formInfo.getPath();
			File file=new File(str.substring(0,str.lastIndexOf("/")));    
	   		if(!file.exists()) {
	   			file.mkdir();
		    }
	   	 System.out.println("formpath==="+File.separator+"projects"+ File.separator+formInfo.getPath());
			XMLHelper.saveDocument(doc,File.separator+"projects"+ File.separator+formInfo.getPath());//会自动覆盖原内容；
		}catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
    }
}
