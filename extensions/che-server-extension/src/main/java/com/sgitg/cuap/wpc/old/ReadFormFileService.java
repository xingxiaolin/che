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
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sgitg.cuap.pojo.VFormInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.inject.Inject;

@Path("/readFormFileService")
public class ReadFormFileService {

	@Inject
	public ReadFormFileService(){
	}

	@Path("/readFormFile/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
    public VFormInfo readFormFile(VFormInfo v ){
		return  getFormByXml(v);
    }
    
	public static VFormInfo getFormByXml(VFormInfo v){
   		String path =  File.separator+"projects"+ File.separator + v.getPath();
   		System.out.println("path="+path);
   		File file=new File(path);    
   		if(!file.exists()) {
   			//file.mkdir();
   			return null;
   		}
   		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   		factory.setIgnoringElementContentWhitespace(true);
   		Element element = null;
   		try {
   			DocumentBuilder db = factory.newDocumentBuilder();
   			Document xmldoc = db.parse(new File(path));
   			element = xmldoc.getDocumentElement();
   			v = xmlToForm(new VFormInfo(), element);
   		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   		return v;
   	}
	
	@SuppressWarnings("unused")
	public static VFormInfo  xmlToForm(VFormInfo vform, Node node) {
		// 添加FORM节点; 为节点类型，输出节点名称
		if (node.getNodeType() == Node.ELEMENT_NODE	&& node.getNodeName().equalsIgnoreCase("FORM")) {
			NamedNodeMap Attrmap = node.getAttributes();// 当前节点的所有属性的map
			String id = Attrmap.getNamedItem("ID").getNodeValue()==null ?Attrmap.getNamedItem("id").getNodeValue():Attrmap.getNamedItem("ID").getNodeValue();
			vform.setRemark(id);
		}
		// 获取所要遍历节点的子节点
		NodeList allNodes = node.getChildNodes();		
		for(int i=0; allNodes!=null && i<allNodes.getLength();i++){
				Node childNode = allNodes.item(i);
				String name = childNode.getNodeName();
				String value = childNode.getNodeValue();			
				if (childNode.getNodeType() == Node.ELEMENT_NODE	&& childNode.getNodeName().equalsIgnoreCase(name)) {
					NamedNodeMap map = childNode.getAttributes();// 当前节点的所有属性的map
					String nodevalue = "" + childNode.getFirstChild().getNodeValue();
					if(name.equalsIgnoreCase("id")){
						vform.setId(nodevalue);
					}
					if(name.equalsIgnoreCase("name")){
						vform.setName(nodevalue);
					}
					if(name.equalsIgnoreCase("title")){
						vform.setTitle(nodevalue);
					}
					if(name.equalsIgnoreCase("FULL_NAME")){
						vform.setFullName(nodevalue);
					}
					if(name.equalsIgnoreCase("service")){
						vform.setService(nodevalue);
					}
					if(name.equalsIgnoreCase("script")){
						vform.setScript(nodevalue);
					}
					if(name.equalsIgnoreCase("SCRIPT_CLASS")){
						vform.setScriptClass(nodevalue);
					}
					if(name.equalsIgnoreCase("css")){
						vform.setCss(nodevalue);
					}
					if(name.equalsIgnoreCase("BUNDLE_ID")){
						vform.setBundleId(nodevalue);
					}
					if(name.equalsIgnoreCase("type")){
						vform.setType(nodevalue);
					}
					if(name.equalsIgnoreCase("data")){
						vform.setData(nodevalue);
					}
					if(name.equalsIgnoreCase("DATA_ID")){
						vform.setDataId(nodevalue);
					}
				}
		}
		return vform;
	}
}