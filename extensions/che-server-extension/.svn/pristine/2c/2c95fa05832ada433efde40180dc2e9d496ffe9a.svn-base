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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sgitg.cuap.pojo.VFormInfo;
import com.sgitg.cuap.wpc.common.DataException;

@Path("/readJarFormFileService")
public class ReadJarFormFileService {

	@Inject
	public ReadJarFormFileService(){
	}

    @Path("/readFormFile/{a},{b}")
    @GET
    @Produces("application/json")
    public VFormInfo readFormFile(@PathParam("a") String bundleName,@PathParam("b") String fileName){
    	VFormInfo formInfo = new VFormInfo();
    	try {
			Map<String, String> rs = loadFromResource(fileName);
			if(rs == null || rs.isEmpty()){
				return null;
			}
			formInfo.setId("" + rs.get("ID"));
			formInfo.setName("" + rs.get("NAME"));
			formInfo.setTitle("" + rs.get("TITLE"));
			formInfo.setFullName("" + rs.get("FULL_NAME"));
			formInfo.setBundleId("" + rs.get("BUNDLE_ID"));
			formInfo.setService("" + rs.get("SERVICE"));
			formInfo.setScript("" + rs.get("SCRIPT"));
			formInfo.setScriptClass("" + rs.get("SCRIPT_CLASS"));
			formInfo.setCss("" + rs.get("CSS"));
			formInfo.setType("" + rs.get("TYPE"));
			formInfo.setDataId("" + rs.get("DATA_ID"));
			formInfo.setData("" + rs.get("DATA"));
		} catch (DataException e) {
			e.printStackTrace();
		}
    	return formInfo;
    }
    
    /**
     * 根据文件名(带路径)返回流
     */
    public  Map<String, String> loadFromResource(@PathParam("a") String resourceName) throws DataException{
    	InputStream is = null;
		try{
			is = getClass().getResourceAsStream("/com/sgitg/cuap/wpc/designer/client/"+resourceName);
			if(is == null){
				return null;
			}else{
				return loadFromInputStream(is);
			}
		} catch(Exception e){
			throw new DataException("loadFromResource(" + resourceName + ") error : " + e.getMessage(), e);
		} finally{
			if(is != null){
				try{
					is.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
    }

    /**
     * 将文件流转换成DOM文件
     */
    public Map<String, String> loadFromInputStream(InputStream is) throws DataException{
    	Document doc = null;
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			doc = builder.parse(is);
			return loadFromDocument(doc);
		} catch (Exception e) {
			throw new DataException("loadFromInputStream(InputStream) error : "+e.getMessage(),e);
		}
    }

    /**
     * 将DOM文件转换成MAP
     */
    public static Map<String, String> loadFromDocument(Document doc) throws DataException{
		Element root = doc.getDocumentElement();
		Map<String, String> map = new HashMap<String, String>();
		NodeList nl = root.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++){
//			System.out.println(nl.item(i).getNodeName() +"="+nl.item(i).getTextContent());
			map.put(nl.item(i).getNodeName(), nl.item(i).getTextContent());
		}
		return map;
	}
}