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
import java.io.StringReader;
import java.util.UUID;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.inject.Inject;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;


@Path("/saveFileService")
public class SaveFileService{
	
	@Inject
	public SaveFileService(){
	}	
	
	
	@Path("/saveFile/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public boolean saveFile(String str){
		str =str.substring(9,str.length()-2).replace("\\\"", "\"");
		System.out.println("2222="+str+"$$");
		String id = UUID.randomUUID().toString().replace("-", "");
		saveDocument(strToDocument(str),File.separator+"projects"+ File.separator+"ttt"+File.separator+id+".xml");
		return true;
    }	
	
	/**
	 * 字符串转换成Document对象
	 */
	public static Document  strToDocument(String xmlStr){
		Document doc =null;
		try {
			System.out.println("777"+xmlStr);
			StringReader str = new StringReader(xmlStr); 
			System.out.println("888"+str);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(str));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc; 
	}
	
	/**
	 * 写文件到容器中
	 * @param doc  DOM文件
	 * @param fileName 文件名
	 */
	public static void saveDocument(Document doc,String fileName) {
		System.out.println("fileName="+fileName);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(doc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(fileName));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	  * 实现dom4j向org.w3c.dom.Document的转换
	  * @param doc
	  * @return
	  * @throws Exception
	  */
//	 public static org.w3c.dom.Document parse(Document doc) throws Exception {
//	  if (doc == null) {
//	   return (null);
//	  }
//	  java.io.StringReader reader = new java.io.StringReader(doc.toString());
//	  org.xml.sax.InputSource source = new org.xml.sax.InputSource(reader);
//	  javax.xml.parsers.DocumentBuilderFactory documentBuilderFactory = javax.xml.parsers.DocumentBuilderFactory
//	    .newInstance();
//	  javax.xml.parsers.DocumentBuilder documentBuilder = documentBuilderFactory
//	    .newDocumentBuilder();
//	  return (documentBuilder.parse(source));
//	 }
}
