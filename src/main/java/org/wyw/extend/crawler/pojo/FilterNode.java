package org.wyw.extend.crawler.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class FilterNode {

	private String name;
	private Map<String, String> attrs = new HashMap<String, String>();
	private FilterNode next;
	
	public FilterNode(String name) {
		this.name = name;
	}
	
	public boolean match(Node node){
		if(name!=null && !node.getNodeName().equalsIgnoreCase(name))
			return false;
		if(attrs==null || attrs.size()==0)
			return true;
		NamedNodeMap nodeMap = node.getAttributes();
		if(nodeMap==null || nodeMap.getLength()<attrs.size())
			return false;
		for(Entry<String, String> entry: attrs.entrySet()){
			Node idNode = nodeMap.getNamedItem(entry.getKey());
			if(idNode == null)
				return false;
			String attrVal = idNode.getNodeValue();
			if(attrVal == null || !attrVal.trim().equals(entry.getValue()))
				return false;
		}
		return true;
	}
	
	public FilterNode addAttr(String key, String value){
		attrs.put(key, value);
		return this;
	}
	
	public FilterNode addNode(String nodeName){
		next = new FilterNode(nodeName);
		return next;
	}
	
	public boolean hasNext(){
		return next!=null;
	}
	
	public FilterNode next(){
		return next;
	}
	
}
