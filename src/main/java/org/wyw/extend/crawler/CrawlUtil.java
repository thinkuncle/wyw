package org.wyw.extend.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.wyw.extend.crawler.pojo.FilterNode;
import org.wyw.extend.crawler.pojo.Page;
import org.w3c.dom.Node;

/**
 * 爬取工具类
 * 
 * @author wanghaibo
 *
 */
public class CrawlUtil {

	public static final Logger log = Logger.getLogger(CrawlUtil.class);

	/**
	 * 获取匹配结果
	 * @param url 爬取URL
	 * @param contentPattern 匹配内容正则
	 * @return
	 * @throws Exception
	 */
	public static Matcher getMatcher(String url, Pattern contentPattern) throws Exception {
		Matcher matcher = null;
		try {
			URL u = new URL(url);
			Page page = new Page(u);
			Fetcher fetchJob = new Fetcher();
			fetchJob.fetch(page);
			DecodeUtils.decode(page);
			String cnt = page.getContentStr();
			matcher = contentPattern.matcher(cnt);
		} catch (Exception e) {
			throw e;
		}
		return matcher;
	}

	/**
	 * 获取匹配结果
	 * 
	 * @param urly 爬取URL
	 * @param contentPattern 匹配内容正则
	 * @return
	 * @throws Exception
	 */
	public static Node getNode(String url, FilterNode... filterNodes) throws Exception {
		Node node = null;
		try {
			URL u = new URL(url);
			Page page = new Page(u);
			Fetcher fetchJob = new Fetcher();
			fetchJob.fetch(page);
			DecodeUtils.decode(page);
			ParseUtils.parse(page);
			for (FilterNode filterNode : filterNodes) {
				if (filterNode == null) {
					continue;
				}
				node = ExtractUtils.filter(page.getRoot(), filterNode);
				if (node != null) {
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return node;
	}
	
	/**
	 * 获取匹配结果
	 * @param url 爬取URL
	 * @param contentPattern 匹配内容正则
	 * @return
	 * @throws Exception 
	 */
	public static String getContent(String url) throws IOException{
		try{
			URL u = new URL(url);
			Page page = new Page(u);
			Fetcher fetchJob = new Fetcher();
			fetchJob.fetch(page);
			DecodeUtils.decode(page);
			return page.getContentStr();
		}catch (Exception e) {
			throw new IOException("访问异常,"+ e.getMessage());
		}
	}

}
