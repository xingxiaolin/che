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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.FileNotFoundException;

public class XMLHelper {
	
	/**
	 * 写文件到容器中
	 * @param doc  DOM文件
	 * @param path 文件名(包括路径)
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
		}
	}
	
	/**
	 * 写文件到容器中
	 * @param doc  DOM文件
	 * @param path 文件名(包括路径)
	 */
	public static void saveDocument2(Document doc, String path){
		File f = new File(path.substring(0,path.lastIndexOf("/")));
		if (!f.exists()) {
			f.mkdirs();
        }
		String xmlcontent =getStringFromDocument(doc);
		FileOutputStream out = null;
		try{			
			out = new FileOutputStream(path);
			out.write(xmlcontent.getBytes());
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			try{
				out.close();
			}catch(Exception e){
				
			};
		}
	}
	
	public static Element getFirstElement(Node root, String name){
		return getFirstElement(root, name, false);
	}

	@SuppressWarnings("rawtypes")
	public static Element getFirstElement(Node root, String name, boolean autoCreate){
		List es = getElementList(root, name);
		if(es.size() < 1){
			if(autoCreate){
				Node n = root;
				while(n != null && !(n instanceof Document))n = root.getParentNode();
				if(n == null)return null;
				Element e = ((Document)n).createElement(name);
				root.appendChild(e);
				return e;
			}else{
				return null;
			}
		}
		return (Element) es.get(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Element[] getElement(Node root, String name){
		List al = getElementList(root, name);
		Element[] es = new Element[al.size()];
		al.toArray(es);
		return es;
	}
	
	/**
	 * 将nodeLst转换成mapLst
	 * @param nodeLst
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<HashMap> transList(List<Node> nodeLst){
		List<HashMap> retLst = new ArrayList<HashMap>();
		if(nodeLst!=null && nodeLst.size()>0){
			for(int i=0; i<nodeLst.size(); i++){
				Node tempNode = nodeLst.get(i);
				HashMap map = new HashMap();
				retLst.add(map);
				NamedNodeMap nnm = tempNode.getAttributes();
				for(int j=0; j<nnm.getLength(); j++){
					Attr attr = (Attr)nnm.item(j);
					map.put(attr.getName(), attr.getValue());
				}
			}
		}
		return retLst;
	}
	
	/**
	 * 将nodeLst转换成mapLst
	 * @param nodeLst
	 * @param isLowerCase 是否将dom中的属性名小写处理
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> transList(List<Node> nodeLst, boolean isLowerCase){
		List<Map> retLst = new ArrayList<Map>();
		if(nodeLst!=null && nodeLst.size()>0){
			for(int i=0; i<nodeLst.size(); i++){
				Node tempNode = nodeLst.get(i);
				Map map = new HashMap();
				retLst.add(map);
				NamedNodeMap nnm = tempNode.getAttributes();
				for(int j=0; j<nnm.getLength(); j++){
					Attr attr = (Attr)nnm.item(j);
					map.put(isLowerCase ? attr.getName().toLowerCase() : attr.getName(), attr.getValue());
				}
			}
		}
		return retLst;
	}
	
	/**
	 * 将nodeLst转换成mapLst
	 * @param nodeLst
	 * @param attrNames 指定的属性名（返回的结果中包含指定的属性名）
	 * @param isLowerCase 是否将dom中的属性名小写处理
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> transList(List<Element> nodeLst, String[] attrNames, boolean isLowerCase){
		List<Map> retLst = new ArrayList<Map>();
		if(nodeLst!=null && nodeLst.size()>0){
			for(int i=0; i<nodeLst.size(); i++){
				Element tempNode = nodeLst.get(i);
				Map map = new HashMap();
				int nullAttrCount = 0;
				for (int j=0; j<attrNames.length; j++){
					Object prop = tempNode.getAttribute(attrNames[j]);
					if(null == prop || "".equals(prop + "")){
						nullAttrCount ++;
					}
					map.put(isLowerCase ? attrNames[j].toLowerCase() : attrNames[j], prop);
				}
				if(attrNames.length > nullAttrCount){
					retLst.add(map);
				}
				
			}
		}
		return retLst;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getElementList(Node root, String name){
		NodeList nl = null;
		ArrayList al = new ArrayList();
		if(root instanceof Document){
			nl = ((Document)root).getElementsByTagName(name);
		}else if(root instanceof Element){
			nl = ((Element)root).getElementsByTagName(name);
		}else{
			return al;
		}
		for(int i = 0; i < nl.getLength(); i ++){
			Element e = (Element)nl.item(i);
			if(e.getNodeName().equals(name)){
				al.add(e);
			}
		}
		return al;		
	}
	
	public static Document getDocument(String path){
		InputStream is = null;
		try{
			is = new FileInputStream(path);
			return getDocumentFromStream(is);
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			try {
				if(is != null)is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Document getDocumentFromStream(InputStream is){
		Document doc = null;
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			doc = builder.parse(is);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static String getStringFromDocument(Document doc){
		try{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding", "UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			String xmlcontent = bos.toString();
			return xmlcontent;
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] getByteArrFromDocument(Document doc){
		try{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding", "UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			return bos.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	
	public static Document createDocument(String rootName){
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		Document doc = builder.newDocument();
		Element rootElement = doc.createElement(rootName);
		doc.appendChild(rootElement);
		return doc;
	}
	
	public static InputStream getStreamFromDocument(Document doc){
		String xmlcontent =getStringFromDocument(doc);
		return new ByteArrayInputStream(xmlcontent.getBytes());
	}
	
	public static Document loadDocument(String xmlStr){
		Document doc = null;
		if(!(xmlStr==null || "".equals(xmlStr.trim()) || "null".equals(xmlStr.trim()))){
			try {
				StringReader sr = new StringReader(xmlStr); 
				InputSource is = new InputSource(sr); 
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.parse(is);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return doc;
	}
}
