package org.wyw.extend.crawler.pojo;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;

import org.wyw.extend.crawler.HTMLMetaTags;
import org.w3c.dom.Node;


public class Page {

	private URL url;
	private int responseCode;
	private Map<String, String> headers;
	private ByteBuffer content;
	private String encoding;
	private HTMLMetaTags metaTags;
	private Node root;
//	private PageStatus status = PageStatus.READY;
	
	public Page(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
	
	public ByteBuffer getContent() {
		return content;
	}
	
	public void setContent(ByteBuffer content) {
		this.content = content;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getFromHeaders(String key){
		return headers==null?null:headers.get(key);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node node) {
		this.root = node;
	}

	public HTMLMetaTags getMetaTags() {
		return metaTags;
	}

	public void setMetaTags(HTMLMetaTags metaTags) {
		this.metaTags = metaTags;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getContentStr(){
		try {
			return new String(content.array(),encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
}
