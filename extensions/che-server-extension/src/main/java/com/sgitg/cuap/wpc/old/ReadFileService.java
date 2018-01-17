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
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.inject.Inject;
import com.sgitg.cuap.wpc.po.VFormInfo;
import java.io.ByteArrayOutputStream;

@Path("/readFileService")
public class ReadFileService {

	@Inject
	public ReadFileService(){
	}

	@Path("/readFile/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")    
	public static String readFile(VFormInfo v){		
   		String path =  File.separator+"projects"+ File.separator + v.getPath();
   		System.out.println("path="+path);
   		Document xmldoc =null;
   		try {
   			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   			factory.setIgnoringElementContentWhitespace(true);
   			DocumentBuilder db = factory.newDocumentBuilder();
   			xmldoc = db.parse(new File(path));   	
   		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   		System.out.println("返回=="+documentToStr(xmldoc));
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
			xmlStr = bos.toString();
		} catch (Exception e) {
			e.printStackTrace();
	    }
		return xmlStr;
	}
}