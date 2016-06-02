/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wyw.extend.crawler;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.MalformedURLException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.wyw.extend.crawler.pojo.FilterNode;
import org.w3c.dom.*;

public class ExtractUtils {

	public static Node filter(Node node, FilterNode filterNode) {

		NodeWalker walker = new NodeWalker(node);

		while (walker.hasNext()) {

			Node currentNode = walker.nextNode();
			String nodeName = currentNode.getNodeName();
			short nodeType = currentNode.getNodeType();

			if ("script".equalsIgnoreCase(nodeName)) {
				walker.skipChildren();
			}

			if ("style".equalsIgnoreCase(nodeName)) {
				walker.skipChildren();
			}

			if (nodeType == Node.COMMENT_NODE) {
				walker.skipChildren();
			}

			if (filterNode.match(currentNode)) {
				if (filterNode.hasNext())
					filterNode = filterNode.next();
				else {
					return currentNode;
				}
			}
		}
		return null;
	}

	/**
	 * 跳去头部节点 获取章节内容
	 * @param node
	 * @return
	 */
	public static String getText(Node node) {
		StringBuilder sb = new StringBuilder();
		NodeWalker walker = new NodeWalker(node);
		while (walker.hasNext()) {
			Node currentNode = walker.nextNode();
			String nodeName = currentNode.getNodeName();
			short nodeType = currentNode.getNodeType();
			if ("script".equalsIgnoreCase(nodeName)) {
				walker.skipChildren();
			}
			if ("style".equalsIgnoreCase(nodeName)) {
				walker.skipChildren();
			}
			if (nodeType == Node.COMMENT_NODE) {
				walker.skipChildren();
			}
			if (nodeType == Node.TEXT_NODE) {
				// cleanup and trim the value
				String text = currentNode.getNodeValue();
				text = text.replaceAll("\\s+", " ");
				text = text.trim();
				if (text.length() > 0) {
					if (sb.length() > 0)
						sb.append(' ');
					sb.append(text);
				}
			}
		}
		return sb.toString();
	}
	
	public static String getTextByChild(Node node) {
		StringBuilder sb = new StringBuilder();
		NodeList childrens = node.getChildNodes();
	    int childLen = (childrens != null) ? childrens.getLength() : 0;
	    for (int i = 0; i < childLen; i++) {
	    	Node currentNode = childrens.item(i);
	    	if (currentNode.getNodeType() == Node.TEXT_NODE) {
				String text = currentNode.getNodeValue();
				text = text.replaceAll("\\s+", " ");
				text = text.trim();
				if (text.length() > 0) {
					if (sb.length() > 0)
						sb.append(' ');
					sb.append(text);
				}
			}
	    }
		return sb.toString();
	}
	
	public static String getTitle(Node node) {
		StringBuilder sb = new StringBuilder();
		NodeWalker walker = new NodeWalker(node);
		while (walker.hasNext()) {
			Node currentNode = walker.nextNode();
			String nodeName = currentNode.getNodeName();
			short nodeType = currentNode.getNodeType();

			if ("body".equalsIgnoreCase(nodeName)) { // stop after HEAD
				return null;
			}
			if (nodeType == Node.ELEMENT_NODE) {
				if ("title".equalsIgnoreCase(nodeName)) {
					return getText(currentNode);
				}
			}
		}
		return sb.toString();
	}

	public static String toString(Node node) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
//		t.setOutputProperty("encoding", "GB2312");// 解决中文问题，试过用GBK不行
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		t.transform(new DOMSource(node), new StreamResult(bos));
		return bos.toString();
	}

	/** If Node contains a BASE tag then it's HREF is returned. */
	public static URL getBase(Node node) {

		NodeWalker walker = new NodeWalker(node);

		while (walker.hasNext()) {

			Node currentNode = walker.nextNode();
			String nodeName = currentNode.getNodeName();
			short nodeType = currentNode.getNodeType();

			// is this node a BASE tag?
			if (nodeType == Node.ELEMENT_NODE) {

				if ("body".equalsIgnoreCase(nodeName)) { // stop after HEAD
					return null;
				}

				if ("base".equalsIgnoreCase(nodeName)) {
					NamedNodeMap attrs = currentNode.getAttributes();
					for (int i = 0; i < attrs.getLength(); i++) {
						Node attr = attrs.item(i);
						if ("href".equalsIgnoreCase(attr.getNodeName())) {
							try {
								return new URL(attr.getNodeValue());
							} catch (MalformedURLException e) {
							}
						}
					}
				}
			}
		}

		// no.
		return null;
	}

}
