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
package com.sgitg.cuap.wpc;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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
import com.sgitg.cuap.pojo.VFormInfo;

@Path("/fileOperationService")
public class FileOperationService{
	
	@Inject
	public FileOperationService(){
	}	
	//============写文件到容器=========================//
	
	@Path("/saveFile/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public boolean saveFile(VFormInfo v){
		String path = File.separator +"projects"+ File.separator + v.getPath();
		String data = v.getData();
		System.out.println("页面传来的文件数据=="+data);
		//转换传来的字符串:"MMM#MMM"替换为双引号;"YYY#YYY"替换为单引号
		saveDocument(strToDocument(data.replace("MMM#MMM", "\"").replace("YYY#YYY", "\'")),path);
		return true;
    }	
	
	/**
	 * 字符串转换成Document对象
	 */
	public static Document  strToDocument(String xmlStr){
		Document doc =null;
		try {
			System.out.println("转换页面传来的数据=="+xmlStr);
			StringReader str = new StringReader(xmlStr); 
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
		System.out.println("带路径的文件名:=="+fileName);
		File file=new File(fileName.substring(0,fileName.lastIndexOf("/")));    
   		if(!file.exists()) {
   			file.mkdirs();
   		}
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
	
	//===============从容器读取文件===============//	
	@Path("/readFile/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")    
	public static String readFile(VFormInfo v){		
   		String path =  File.separator+"projects"+ File.separator + v.getPath();
   		System.out.println("读取文件带路径=="+path);
   		Document xmldoc =null;
   		try {
   			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   			factory.setIgnoringElementContentWhitespace(true);
   			DocumentBuilder db = factory.newDocumentBuilder();
			xmldoc = db.parse(new File(path));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}   	
   		return documentToStr(xmldoc);
   	}	
	
	/**
	 * Document转换成字符串
	 */
	public static String documentToStr(Document doc){
		String xmlStr = "";
		try {
			TransformerFactory  tf  =  TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding","UTF-8");//解决中文问题，试过用GBK不行
			ByteArrayOutputStream  bos  =  new  ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			xmlStr = bos.toString("UTF-8");//这次转码对不同OS的编码进行转换
		} catch (Exception e) {
			e.printStackTrace();
	    }
		System.out.println("转成字符串后返回=="+xmlStr);
		return xmlStr;
	}	
}
