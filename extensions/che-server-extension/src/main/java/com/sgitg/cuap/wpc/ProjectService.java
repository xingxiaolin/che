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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import com.sgitg.cuap.pojo.AttributeXml;
import com.sgitg.cuap.pojo.Bundle;
import com.sgitg.cuap.pojo.Page;
import com.sgitg.cuap.pojo.VProject;
import com.sgitg.cuap.pojo.TreeItem;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.inject.Inject;

@Path("/projectService")
public class ProjectService {
	public static String _PROJECT = "project";//根节点的节点名
	public static String _WORKSPACE= "workspace";//根节点的节点名
	public static String _BUNDLE = "bundle";//模块节点的节点名
	public static String _PAGE = "page";//叶子节点的节点名

	@Inject
	public ProjectService(){
	}

	@Path("/queryProject/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
    public VProject queryProject(VProject p){
    	VProject project = getXmlByProject(p);
		return project;
    }

	 /**
   	 * 根据xml文件获取相应的project对象
   	 * @param path xml文件路径
   	 * @return project对象
   	 */
   	public VProject getXmlByProject(VProject p){
   		//String absolutePath =new File(".").getAbsolutePath();
   		String path = File.separator + "projects" + File.separator + p.getName() + File.separator + p.getFileName();
   		System.out.println("$$$$$$$$$$$$$$$$$$"+path);
   		File file=new File(path);
   		if(!file.exists()) {
   			return null;
   		}
   		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   		factory.setIgnoringElementContentWhitespace(true);
   		Element theProject = null;
   		VProject project = new VProject();
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(path));
   			theProject = xmldoc.getDocumentElement();
   			project = xmlToProject(project, theProject);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   		return project;
   	}

   	/**
	 * 将xml转成java对象
	 * @param project 需要创建的peoject对象
	 * @param node xml节点
	 * @return 创建好的peoject对象
	 */
	public VProject xmlToProject(VProject project, Node node) {
		// 添加project节点
		String rNodeName = node.getNodeName();// 当前遍历元素名称
//		if (node.getNodeType() == Node.ELEMENT_NODE && rNodeName.equals("workspace")) {
//
//		}
		if (node.getNodeType() == Node.ELEMENT_NODE && rNodeName.equals("project")) { // 为节点类型，输出节点名称
			NamedNodeMap Attrmap = node.getAttributes();// 当前节点的所有属性的map
			project.setId(Attrmap.getNamedItem("id").getNodeValue());
			project.setName(Attrmap.getNamedItem("name").getNodeValue());
			project.setStatus(Attrmap.getNamedItem("status").getNodeValue());
		}
		// 获取所要遍历节点的子节点
		NodeList allNodes = node.getChildNodes();
		int size = allNodes.getLength();
		if (size > 0) {
			List<Bundle> bundles = new ArrayList<Bundle>();
			List<Page> pages = new ArrayList<Page>();
			//遍历节点的子节点
			for (int j = 0; j < size; j++) {
				Node childNode = allNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE
						&& childNode.getNodeName().equals("bundle")) {
					Bundle bundle = new Bundle();
					// 添加bundle
					if (childNode.getChildNodes().getLength() > 0) {
						// 当前bundle有子节点
						List<Bundle> bundles2 = new ArrayList<Bundle>();
						bundle.setBundles(bundles2);
						NamedNodeMap Attrmap = childNode.getAttributes();// 当前节点的所有属性的map
						bundle.setId(Attrmap.getNamedItem("id").getNodeValue());
						bundle.setName(Attrmap.getNamedItem("name").getNodeValue());
						bundle.setStatus(Attrmap.getNamedItem("status").getNodeValue());
						// 添加其他属性
						//递归创建bundle
						bundle = xml2Bundle(bundle, childNode);
					}else{
						//当前bundle没有子节点 给当前bundle添加属性
						NamedNodeMap Attrmap = childNode.getAttributes();// 当前节点的所有属性的map
						bundle.setId(Attrmap.getNamedItem("id").getNodeValue());
						bundle.setName(Attrmap.getNamedItem("name").getNodeValue());
						bundle.setStatus(Attrmap.getNamedItem("status").getNodeValue());
					}
					bundles.add(bundle);
				}
				if (childNode.getNodeType() == Node.ELEMENT_NODE
						&& childNode.getNodeName().equals("page")) {
					Page page = new Page();
					NamedNodeMap Attrmap = childNode.getAttributes();
					page.setId(Attrmap.getNamedItem("id").getNodeValue());
					page.setName(Attrmap.getNamedItem("name").getNodeValue());
					page.setStatus(Attrmap.getNamedItem("status").getNodeValue());
					// 添加其他属性
					// 添加page到集合
					pages.add(page);
				}
			}
			if (bundles.size() > 0) {
				project.setBundles(bundles);
			}
			if (pages.size() > 0) {
				project.setPages(pages);
			}
		}
		return project;
	}

	//=================================================//
    @Path("/saveProject/")
    @POST
	@Consumes("application/json")
	@Produces("application/json")
	public void saveProject(VProject project){
    	//File f = new File(".");
		//String absolutePath = f.getAbsolutePath();
		List<TreeItem> list = projectToList(project);
		VProject newProject=list2Project(list);
		//文件名为空,则直接创建文件夹
		 if(project.getFileName() == null || project.getFileName().length() <= 0){
			projectToXml(newProject,File.separator + "projects"+ File.separator+project.getName());
		}
		else{//文件不为空
			projectToXml(newProject,File.separator + "projects" + File.separator+project.getName() + File.separator+project.getFileName());
		}
    }

    /**
	 * 将project对象转换成xml文件并保存
	 * @param project
	 * @param path
	 * @return 成功返回true
	 */
	public boolean projectToXml(VProject project,String path) {
		 System.out.println("path=="+path);
		 //文件名为空时，只创建一个名称为项目 名称的文件夹
		if(path.indexOf(".")==-1){
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
	        }
			return true;
		}
		//文件名不为空时
		File f = new File(path.substring(0,path.lastIndexOf("/")));
		if (!f.exists()) {
			f.mkdirs();
        }
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		Element theBundle = null, theProject = null;
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.newDocument();
			Element root = xmldoc.createElement(_PROJECT);
			xmldoc.appendChild(root);
			theProject = xmldoc.getDocumentElement();
			// 为根节点添加属性
			theProject = initAttributes(theProject, project);
			// 添加属于根节点的page
			if (project.getPages() != null && project.getPages().size() > 0) {
				theProject = initPages(xmldoc, theProject, project.getPages());
			}
			// 递归添加bundle节点
			if (project.getBundles() != null && project.getBundles().size() > 0) {
				//遍历根节点的所有bundle节点
				for (Bundle bundle : project.getBundles()) {
					theBundle = xmldoc.createElement(_BUNDLE);
					// 为bundle节点添加属性
					theBundle = initAttributes(theBundle, bundle);
					if (bundle.getPages() != null && bundle.getPages().size() > 0) {
						//为bundle添加pages
						theBundle = initPages(xmldoc, theBundle, bundle.getPages());
					}
					if (bundle.getBundles() != null && bundle.getBundles().size() > 0) {
						//bundle节点还有子节点 调用递归方法
						theBundle = initBundles(xmldoc, theBundle, bundle);
					}
					//将创建好的bundle节点加入到根节点
					theProject.appendChild(theBundle);
				}
			}
			//输出到文件
			saveXml(path, xmldoc);
			//打印xml到控制台
			//output(theProject);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return true;
	}
    //======================================================//
	@Path("/queryList/{a}")
    @GET
    @Produces("application/json")
    public List<TreeItem> queryList(@PathParam("a") String pathfile){
    	VProject project = getXml_project(pathfile);
		List<TreeItem> list = projectToList(project);
		return list;
    }

    /**
   	 * 根据xml文件获取相应的project对象
   	 * @param path xml文件路径
   	 * @return project对象
   	 */
   	public VProject getXml_project(@PathParam("a") String resourceName){
   		String absolutePath =new File(".").getAbsolutePath();
   		String path = absolutePath.substring(0,absolutePath.length()-1) + resourceName + ".xml";
   		File file=new File(path);
   		if(!file.exists()) {
   			//file.mkdir();
   			return null;
   		}
   		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   		factory.setIgnoringElementContentWhitespace(true);
   		Element theProject = null;
   		VProject project = new VProject();
   		try {
   			DocumentBuilder db = factory.newDocumentBuilder();
   			Document xmldoc = db.parse(new File(path));
   			theProject = xmldoc.getDocumentElement();
   			project = xmlToProject(project, theProject);
   		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   		return project;
   	}

	/**
	 * 将project对象转换成前端可用的list集合
	 * @param project 需要转换的project对象
	 * @return 转换好的List<TreeItem>
	 */
	public List<TreeItem> projectToList(VProject project) {
		List<TreeItem> TreeItems = new ArrayList<TreeItem>();
		if(project==null){//没有数据返回空的list
			return TreeItems;
		}
		TreeItem itemItrm = new TreeItem();
		itemItrm = makeTreeItem("", project);
		TreeItems.add(itemItrm);
		if (project.getBundles() != null && project.getBundles().size() > 0) {
			// 递归 将project里面的bundle加入到集合
			for (Bundle bundle : project.getBundles()) {
				TreeItems = bundle2List(TreeItems, bundle, project.getId());
			}
		}
		if (project.getPages() != null && project.getPages().size() > 0) {
			// 将project里面的page加入到集合
			for (Page page : project.getPages()) {
				TreeItem itemItrm2 = new TreeItem();
				itemItrm2 = makeTreeItem(project.getId(), page);
				TreeItems.add(itemItrm2);
			}
		}
		return TreeItems;
	}
	/**
	 * 将不同的节点类型转换成 标准的树节点的java对象
	 * @param parent_id 父节点的id
	 * @param pro
	 * @return 一个树节点的java对象
	 */
	public TreeItem makeTreeItem(String parent_id, VProject pro) {
		TreeItem item = new TreeItem();
		if (pro.getId() != null) {
			item.setId(pro.getId());
		}
		if (pro.getName() != null) {
			item.setName(pro.getName());
		}
		if (pro.getStatus() != null) {
			item.setStatus(pro.getStatus());
		}
		item.setParent_id(parent_id);
		item.setIs_leaf(false);
		return item;
	}
	/**
	 * 递归 将bundle添加到集合
	 * @param TreeItems
	 * @param bundle
	 * @return
	 */
	public List<TreeItem> bundle2List(List<TreeItem> TreeItems, Bundle bundle,String parent_id) {
		TreeItem item = makeTreeItem(parent_id, bundle);
		TreeItems.add(item);
		if (bundle.getPages() != null && bundle.getPages().size() > 0) {
			for (Page page : bundle.getPages()) {
				TreeItems.add(makeTreeItem(bundle.getId(), page));
			}
		}
		if (bundle.getBundles() != null && bundle.getBundles().size() > 0) {
			for (Bundle bundle2 : bundle.getBundles()) {
				TreeItems = bundle2List(TreeItems, bundle2, bundle.getId());
			}
		}
		return TreeItems;
	}
	/**
	 * 将不同的节点类型转换成 标准的树节点的java对象
	 * @param parent_id 父节点的id
	 * @param pro
	 * @return 一个树节点的java对象
	 */
	public TreeItem makeTreeItem(String parent_id, Page page) {
		TreeItem item = new TreeItem();
		if (page.getId() != null) {
			item.setId(page.getId());
		}
		if (page.getName() != null) {
			item.setName(page.getName());
		}
		if (page.getStatus() != null) {
			item.setStatus(page.getStatus());
		}
		item.setParent_id(parent_id);
		item.setIs_leaf(true);
		return item;
	}
	/**
	 * 将不同的节点类型转换成 标准的树节点的java对象
	 * @param parent_id 父节点的id
	 * @param bundle
	 * @return 一个树节点的java对象
	 */
	public TreeItem makeTreeItem(String parent_id, Bundle bundle) {
		TreeItem item = new TreeItem();
		if (bundle.getId() != null) {
			item.setId(bundle.getId());
		}
		if (bundle.getName() != null) {
			item.setName(bundle.getName());
		}
		if (bundle.getStatus() != null) {
			item.setStatus(bundle.getStatus());
		}
		item.setParent_id(parent_id);
		item.setIs_leaf(false);
		return item;
	}
	/**
	 * 递归将bundle节点转成java对象
	 *
	 * @param bundle 需要创建的bundle对象
	 * @param node xm节点
	 * @return 创建好的bundle对象
	 */
	public Bundle xml2Bundle(Bundle bundle, Node node) {
		List<Bundle> bundles = new ArrayList<Bundle>();
		List<Page> pages = new ArrayList<Page>();
		NodeList allNodes = node.getChildNodes();
		int size = allNodes.getLength();
		if (size > 0) {
			// 当前bundle有子节点
			for (int j = 0; j < size; j++) {
				// 遍历子节点
				Node childNode = allNodes.item(j);
				if (childNode.getNodeType() == Node.ELEMENT_NODE
						&& childNode.getNodeName().equals("bundle")) {
					// 当前bundle还有bundle子节点
					Bundle bundle2 = new Bundle();
					NamedNodeMap Attrmap = childNode.getAttributes();

					bundle2.setId(Attrmap.getNamedItem("id").getNodeValue());
					bundle2.setName(Attrmap.getNamedItem("name").getNodeValue());
					bundle2.setStatus(Attrmap.getNamedItem("status").getNodeValue());
					// 递归调用
					bundle2 = xml2Bundle(bundle2, childNode);
					bundles.add(bundle2);
				}
				if (childNode.getNodeType() == Node.ELEMENT_NODE
						&& childNode.getNodeName().equals("page")) {
					// 当前bundle添加pages
					Page page = new Page();
					NamedNodeMap Attrmap = childNode.getAttributes();
					page.setId(Attrmap.getNamedItem("id").getNodeValue());
					page.setName(Attrmap.getNamedItem("name").getNodeValue());
					page.setStatus(Attrmap.getNamedItem("status").getNodeValue());
					pages.add(page);
				}
			}
			if (pages.size() > 0) {
				bundle.setPages(pages);
			}
			if (bundles.size() > 0) {
				bundle.setBundles(bundles);
			}
		}
		return bundle;
	}


	@Path("/updateFileInfo/{a}&{b}")
    @GET
	@Consumes("application/json")
	@Produces("application/json")
	/**
     * 修改指定空间下的项目XML文件(导入的)中的旧的项目ID和项目名称
     * @param projectId 新项目ID
     * @param newProjectName 新项目名称
     * @exception ParserConfigurationException|SAXException|IOException
     */
    public void updateFileInfo(@PathParam("a")String projectId,@PathParam("b")String newProjectName){
        System.out.println("a==/"+projectId);
        System.out.println("b==/"+newProjectName);
//		File xmlFile  = new File(File.separator +"projects"+ File.separator + newProjectName + File.separator  + oldProjectName + ".xml");
        File newFile = new File(File.separator +"projects"+ File.separator + newProjectName + File.separator  + "project.xml");
//        if (xmlFile.renameTo(newFile)) {
        	 DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder builder;
     		try {
     			builder = builderFactory.newDocumentBuilder();
     			Document doc = builder.parse(newFile);
     	        doc.getDocumentElement().normalize();
     	        NodeList nList = doc.getElementsByTagName("project");
     	        Node node = nList.item(0);
     	        Element ele = (Element)node;
     	        //修改项目ID和名称
//     	        System.out.println("old id=="+ ele.getAttributes().getNamedItem("id").getNodeValue());
//     	        System.out.println( "old name=="+ele.getAttributes().getNamedItem("name").getNodeValue());
     	        ele.getAttributes().getNamedItem("id").setTextContent(projectId);
     	        ele.getAttributes().getNamedItem("name").setTextContent(newProjectName);
     	        saveXml(newFile.getPath(),doc);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			catch ( SAXException  e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
//        }
    }

	//============================以下方法暂时不用===================================//
//	@Path("/addNode/")
//	@POST
//	@Consumes("application/json")
//	@Produces("application/json")
		public boolean addTreeNode(Object node) {
			//String path=_PATH;
		    String absolutePath = new File(".").getAbsolutePath();
			String path = absolutePath.substring(0,absolutePath.length()-1) + "tree.xml";
			TreeItem item=initTreeItem(node);
			//根据路径加载Xml并转换成bean
			VProject project=getXml_project(path);
			//将bean转换成 list
			List<TreeItem> list=projectToList(project);
			Boolean isNew=true;//新增的节点是否存在的标志 默认不存在
			//遍历旧的集合 看新增的节点是否已经存在
			for(TreeItem chitem:list){
				if(chitem.getId().equals(item.getId())){
					//如果存在 则更新
					isNew=false;
					chitem.setName(item.getName());
					//状态变成1:修改状态
					chitem.setStatus("1");
				}
			}
			//如果不存在 将需要添加的TreeItem加入到list集合
			if(isNew){
				list.add(item);
			}
			//改变相应节点的状态
			list=editStatus(list,item);
			//将新的list转换成bean
			VProject newProject=list2Project(list);
			//将bean转换成xml 并写入到相应的文件
			projectToXml(newProject,path);
			return true;
		}

//	@POST
//	@Path("/addPage/")
//	@Consumes("application/json")
//	@Produces("application/json")
	public boolean addTreePage(Object parentNode, Object node) {
		//String path=_PATH;
		String absolutePath = new File(".").getAbsolutePath();
		String path = absolutePath.substring(0,absolutePath.length()-1) + "tree.xml";;
		TreeItem item=initTreeItem(node);
		TreeItem parentItem=initTreeItem(parentNode);
		//根据路径加载Xml并转换成bean
		VProject project=getXml_project(path);
		//将bean转换成 list
		List<TreeItem> list=projectToList(project);
		Boolean parentExist=false;//新增的节点的父节点是否存在的标志 默认不存在
		//遍历旧的集合 看新增的节点的父节点是否已经存在
		for(TreeItem chitem:list){
			if(chitem.getId().equals(parentItem.getId())){
				parentExist=true;
				//如果存在 则更新
				chitem.setName(parentItem.getName());
				//状态变成1:修改状态
				chitem.setStatus("1");
			}
		}
		if(!parentExist){
			//新增的节点的父节点不存在则添加
			list.add(parentItem);
		}
		Boolean isNew=true;//新增的节点是否存在的标志 默认不存在
		//遍历旧的集合 看新增的节点是否已经存在
		for(TreeItem chitem:list){
			if(chitem.getId().equals(item.getId())){
				//如果存在 则更新
				isNew=false;
				chitem.setName(item.getName());
				//状态变成1:修改状态
				chitem.setStatus("1");
			}
		}
		//如果不存在 将需要添加的TreeItem加入到list集合
		if(isNew){
			list.add(item);
		}
		//改变相应节点的状态
		list=editStatus(list,item);
		//将新的list转换成bean
		VProject newProject=list2Project(list);
		//将bean转换成xml 并写入到相应的文件
		projectToXml(newProject,path);
		return true;
	}

//	 @Path("/delNode/")
//	 @PUT
//	 @Consumes("application/json")
	public boolean delTreeNode(Object node) {
		//String path=_PATH;
		String absolutePath = new File(".").getAbsolutePath();
		String path = absolutePath.substring(0,absolutePath.length()-1) + "tree.xml";;
		TreeItem item=initTreeItem(node);
		//判断节点类型
		boolean isleaf=item.getIs_leaf();
		//根据路径加载Xml并转换成bean
		VProject project=getXml_project(path);
		//将bean转换成 list
		List<TreeItem> allList=projectToList(project);
		if(isleaf){
			//叶子节点 只需要'删除'一个节点
			//遍历集合找到要删除的节点
			for(TreeItem treeItem:allList){
				if(treeItem.getId().equals(item.getId())){
					//将目标节点的status属性置为-1
					treeItem.setStatus("-1");
					break;
				}
			}
		}else{
			//模块节点 需要'删除'整个模块
			List<TreeItem> dellist=new ArrayList<TreeItem>();//需要删除的所有的节点集合
			List<TreeItem> childlist=new ArrayList<TreeItem>();//目标节点的所有子节点
			//将当前模块加入待删除的集合
			dellist.add(item);
			//1.找到要删除的节点的所有子节点
			for(TreeItem treeItem:allList){
				if(treeItem.getParent_id().equals(item.getId())){
					childlist.add(treeItem);
				}
			}
			if(childlist.size()>0){
				//有子节点
				//遍历子节点集合
				for(TreeItem treeItem:childlist){
					//调用递归删除方法,获得需要删除的所有的节点
					if(treeItem.getIs_leaf()){
						//如果是叶子节点则不需要递归
						dellist.add(treeItem);
					}else{
						//如果是bundle节点 递归
						//将当前节点加入到待删集合
						dellist.add(treeItem);
						dellist=delBundle(allList,dellist,treeItem);
					}
				}
				//遍历获得的待删除的节点集合
				for(TreeItem item2:dellist){
					//遍历所有的节点集合 找到待删除的节点 并将status置为-1
					for(TreeItem treeItem:allList){
						if(treeItem.getId().equals(item2.getId())){
							//将目标节点的status属性置为-1
							treeItem.setStatus("-1");
						}
					}
				}
			}else{
				//没有子节点
				//遍历集合找到要删除的节点
				for(TreeItem treeItem:allList){
					if(treeItem.getId().equals(item.getId())){
						//将目标节点的status属性置为-1
						treeItem.setStatus("-1");
						break;
					}
				}
			}
		}
		//改变相应节点的状态
		allList=editStatus(allList,item);
		//将新的list转换成bean
		VProject newProject=list2Project(allList);
		//将bean转换成xml 并写入到相应的文件
		//List list=projectToList(newProject);
		projectToXml(newProject,path);
		return true;
	}
	/**
	 * 递归删除子节点
	 * @param allList 所有的节点的集合
	 * @param delList 待删除的节点的集合
	 * @param item 当前节点
	 * @return 返回待删除的所有节点
	 */
//	 @Path("/delBundel/")
//	 @PUT
//	 @Consumes("application/json")
	public List<TreeItem>  delBundle(List<TreeItem> allList,List<TreeItem> delList,TreeItem item){
		//判断节点类型
		boolean isleaf=item.getIs_leaf();
		if(!isleaf){
			//如果是bundle节点则递归
			List<TreeItem> childlist=new ArrayList<TreeItem>();//存放当前节点的所有子节点
			//1.遍历所有节点找到当前节点的所有子节点
			for(TreeItem treeItem:allList){
				if(treeItem.getParent_id().equals(item.getId())){
					childlist.add(treeItem);
				}
			}
			if(childlist.size()>0){
				//当前节点还有子节点
				for(TreeItem treeItem:childlist){
					if(!treeItem.getIs_leaf()){
						//如果是bundle节点
						//将当前模块加入待删除的集合
						delList.add(treeItem);
						delList=delBundle(allList,delList,treeItem);
					}else{
						//将当前节点加入待删除的集合
						delList.add(treeItem);
					}
				}
			}
		}
		return delList;
	}

	/**
	 * 将map转换成TreeItem
	 * @param node
	 * @return 转换好的TreeItem对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TreeItem initTreeItem(Object node){
		TreeItem item=new TreeItem();
		Map<String,Object> map=(Map)node;
		item.setId(map.get("id")+"");
		item.setName(map.get("name")+"");
		item.setParent_id(map.get("parent_id")+"");
		item.setStatus(map.get("status")+"");
		item.setIs_leaf((Boolean)map.get("is_leaf"));
		return item;
	}
	/**
	 * 当一个节点被修改时 需要将被修改的节点所有的上层bundle的status置为1:修改状态
	 * @param list 所有的节点的集合
	 * @param item 被修改的节点
	 * @return
	 */
	public List<TreeItem> editStatus(List<TreeItem> AllList,TreeItem editItem){
		TreeItem newItem=editItem;
		//当前节点的父节点的parent_id!=""时一直循环
		while(!newItem.getParent_id().equals("")){
			for(TreeItem item:AllList){
				if(item.getId().equals(newItem.getParent_id())){
					//找到父节点 将status置为1
					item.setStatus("1");
					newItem=item;
				}
			}
		}
		return AllList;
	}
	/**
	 * list<TreeItem>转换成VProject对象
	 * @param TreeItems 所有的树节点
	 * @return 转换好的VProject对象
	 */
	public VProject list2Project(List<TreeItem> AllTreeItems) {
		List<Bundle> bundles = new ArrayList<Bundle>();
		List<Page> pages = new ArrayList<Page>();
		VProject project = new VProject();
		// 找出根节点
		for (TreeItem item : AllTreeItems) {
			if (item.getParent_id().equals("")) {
				project = makeBean(item, project);
			}
		}
		List<TreeItem> bundleItems = new ArrayList<TreeItem>();// 所有的bundle节点
		List<TreeItem> pageItems = new ArrayList<TreeItem>();// 所有的page节点
		// 找出所有的一级bundle节点
		for (TreeItem item : AllTreeItems) {
			if (!item.getIs_leaf()	&& item.getParent_id().equals(project.getId())) {
				bundleItems.add(item);
			}
		}
		// 找出所有的一级page节点
		for (TreeItem item : AllTreeItems) {
			if (item.getParent_id().equals(project.getId()) 	&& item.getIs_leaf()) {
				pageItems.add(item);
			}
		}
		// 创建一级page
		for (TreeItem item : pageItems) {
			Page page = new Page();
			page = makeBean(item, page);
			pages.add(page);
		}
		//创建bundle
		for (TreeItem item : bundleItems) {
			Bundle bundle = new Bundle();
			// 创建当前bundle
			bundle = makeBean(item, bundle);
			// 检查当前bundle是否有子bundle
			// 找出所有属于当前bundle节点的bundle节点
			List<TreeItem> childBundleItems = new ArrayList<TreeItem>();
			for (TreeItem item2 : AllTreeItems) {
				if (item2.getParent_id().equals(item.getId())
						&& !item2.getIs_leaf()) {
					childBundleItems.add(item2);
				}
			}
			if (childBundleItems.size() > 0) {
				//当前bundle有子bundle
				// 找到所有属于当前bundle节点的page节点
				List<Page> childPages = new ArrayList<Page>();
				List<TreeItem> childPageItems = new ArrayList<TreeItem>();
				for (TreeItem item2 : AllTreeItems) {
					if (item2.getParent_id().equals(item.getId())
							&& item2.getIs_leaf()) {
						childPageItems.add(item2);
						Page page = new Page();
						//为page对象添加属性
						page = makeBean(item2, page);
						//将创建好的page添加到集合
						childPages.add(page);
					}
				}
				if (childPages.size() > 0) {
					// 创建当前bundle节点的pages
					bundle.setPages(childPages);
				}
				// 当前bundle节点有bundle子节点
				List<Bundle> childBundles = new ArrayList<Bundle>();
				for (TreeItem item2 : childBundleItems) {
					Bundle bundle2 = new Bundle();
					//递归创建子节点
					bundle2 = list2Bundle(AllTreeItems, item2, bundle2);
					childBundles.add(bundle2);
				}
				if (childBundles.size() > 0) {
					// 创建当前bundle节点的bundles
					bundle.setBundles(childBundles);
				}
			}else{
				//当前bundle没有子bundle 只创建pages
				// 找到所有属于当前bundle节点的所有page节点
				List<Page> childPages = new ArrayList<Page>();
				List<TreeItem> childPageItems = new ArrayList<TreeItem>();
				for (TreeItem item2 : AllTreeItems) {
					if (item2.getParent_id().equals(item.getId())
							&& item2.getIs_leaf()) {
						childPageItems.add(item2);
						Page page = new Page();
						//为page对象添加属性
						page = makeBean(item2, page);
						//将创建好的page添加到集合
						childPages.add(page);
					}
				}
				if (childPages.size() > 0) {
					// 创建当前bundle节点的pages
					bundle.setPages(childPages);
				}
			}
			bundles.add(bundle);
		}
		if (pages.size() > 0) {
			project.setPages(pages);
		}
		if (bundles.size() > 0) {
			project.setBundles(bundles);
		}
		return project;
	}


	/**
	 * 将标准的树节点的java对象 转换成相应类型的节点对象
	 * @param item
	 * @param project
	 * @return
	 */
	public VProject makeBean(TreeItem item, VProject project) {
		project.setId(item.getId());
		if (item.getName() != null) {
			project.setName(item.getName());
		}
		if (item.getStatus() != null) {
			project.setStatus(item.getStatus());
		}
		return project;
	}

	/**
	 * 将标准的树节点的java对象 转换成相应类型的节点对象
	 * @param item
	 * @param page
	 * @return
	 */
	public Page makeBean(TreeItem item, Page page) {
		page.setId(item.getId());
		if (item.getName() != null) {
			page.setName(item.getName());
		}
		if (item.getStatus() != null) {
			page.setStatus(item.getStatus());
		}
		return page;
	}

	/**
	 * 将标准的树节点的java对象 转换成相应类型的节点对象
	 * @param item
	 * @param bundle
	 * @return
	 */
	public Bundle makeBean(TreeItem item, Bundle bundle) {
		bundle.setId(item.getId());
		if (item.getName() != null) {
			bundle.setName(item.getName());
		}
		if (item.getStatus() != null) {
			bundle.setStatus(item.getStatus());
		}
		return bundle;
	}
	/**
	 * 递归 创建bundle
	 * @param TreeItems 所有的bundle节点
	 * @param parent_id 父级节点id
	 * @return 创建好的模块节点
	 */
	public Bundle list2Bundle(List<TreeItem> AllTreeItems,	TreeItem currentItem, Bundle bundle) {
		// 找出当前节点
		for (TreeItem item : AllTreeItems) {
			if (item.getId().equals(currentItem.getId()) && !item.getIs_leaf()) {
				bundle = makeBean(item, bundle);
			}
		}
		// 根据 currentItem.getId()找出属于当前bundle的所有子节点
		List<TreeItem> Items = new ArrayList<TreeItem>();
		for (TreeItem item : AllTreeItems) {
			if (item.getParent_id().equals(currentItem.getId())) {
				Items.add(item);
			}
		}
		// 找出所有属于当前bundle节点的page节点
		List<TreeItem> pageItems = new ArrayList<TreeItem>();
		for (TreeItem item : Items) {
			if (item.getParent_id().equals(currentItem.getId())
					&& item.getIs_leaf()) {
				pageItems.add(item);
			}
		}
		// 创建pages
		List<Page> pages = new ArrayList<Page>();
		for (TreeItem item : pageItems) {
			Page page = new Page();
			page = makeBean(item, page);
			pages.add(page);
		}
		if (pages.size() > 0) {
			bundle.setPages(pages);
		}
		// 检查是否有子bundle节点
		// 找出所有属于当前bundle节点的bundle节点
		List<TreeItem> bundleItems = new ArrayList<TreeItem>();
		for (TreeItem item : Items) {
			if (item.getParent_id().equals(currentItem.getId())
					&& !item.getIs_leaf()) {
				bundleItems.add(item);
			}
		}
		// 如果有子bundle节点 则递归调用
		List<Bundle> bundles = new ArrayList<Bundle>();
		if (bundleItems.size() > 0) {
			for (TreeItem item : bundleItems) {
				Bundle bundle2 = new Bundle();
				bundle2 = list2Bundle(AllTreeItems, item, bundle2);
				bundles.add(bundle2);
			}
		}
		if (bundles.size() > 0) {
			bundle.setBundles(bundles);
		}
		return bundle;
	}
	/**
	 * 为节点添加属性
	 * 类中的属性有AttributeXml注解的会被写入到xml相应节点的属性中
	 * @param element
	 * @param obj 节点的java对象
	 * @return
	 */
	public <T> Element initAttributes(Element element, T obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			AttributeXml attributeXml = field.getAnnotation(AttributeXml.class);
			if (attributeXml != null) {
				//类中的属性有AttributeXml注解的会被写入到xml相应节点的属性中
				try {
					PropertyDescriptor pd = new PropertyDescriptor(
							field.getName(), obj.getClass());
					Method getMethod = pd.getReadMethod();// 获得get方法
					Object value = getMethod.invoke(obj);
					if (value != null) {
						element.setAttribute(field.getName(), value.toString());
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}

			}
		}
		return element;
	}
	/**
	 * 创建page
	 * @param xmldoc
	 * @param theBundle
	 * @param project
	 * @return 创建好的page节点
	 */
	public Element initPages(Document xmldoc, Element element, List<Page> pages) {
		Element ele = null;
		for (Page page : pages) {
			ele = xmldoc.createElement(_PAGE);
			// 为page节点添加属性
			ele = initAttributes(ele, page);
			//将节点加入到父节点
			element.appendChild(ele);
		}
		return element;
	}
	/**
	 * 递归创建bundle节点
	 *
	 * @param xmldoc  Document
	 * @param theBundle
	 * @param bundle
	 * @return 创建好的bundle节点
	 */
	public Element initBundles(Document xmldoc, Element element, Bundle bundle) {
		Element ele = null;
		if (bundle.getBundles() != null && bundle.getBundles().size() > 0) {
			for (Bundle child : bundle.getBundles()) {
				ele = xmldoc.createElement(_BUNDLE);
				// 为bundle节点添加属性
				ele = initAttributes(ele, child);
				if (child.getPages() != null && child.getPages().size() > 0) {
					//添加page节点信息
					ele = initPages(xmldoc, ele, child.getPages());
				}
				if (child.getBundles() != null && child.getBundles().size() > 0) {
					//添加Bundle节点信息
					ele = initBundles(xmldoc, ele, child);
				}
				element.appendChild(ele);
			}
		}
		return element;
	}
	/**
	 * 将Document输出到文件
	 * @param fileName 文件的完整路径
	 * @param doc Document
	 */
	public void saveXml(String fileName, Document doc) {
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
}