package test.wyw.extend.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.wyw.extend.crawler.CrawlUtil;
import org.wyw.util.StringUtil;

public class TestCrawler {

	private static Logger log = Logger.getLogger(TestCrawler.class);
	
	String url = "http://bbs.10jqka.com.cn/codelist.html";
	String codeRootURL = "http://bbs.10jqka.com.cn/";
	String codeReg = "<li>[^<>]*<a href=\"([^<>]*)\" target=\"[^<>]*\" title=\"([^<>]*)\">[^<>]*</a>[^<>]*</li>";
	Pattern pattern = Pattern.compile(codeReg, Pattern.CASE_INSENSITIVE);
	
	/**
	 * 测试爬取站点内容
	 */
	@Test
	public void crawlerTicketr() {
		try {
        	log.info("##########采集股票代码##########");
        	Matcher matcher = CrawlUtil.getMatcher(url, pattern);
        	while(matcher.find()){
				String[] codeArr = matcher.group(1).replace(codeRootURL, "").split(",");
				if(codeArr.length < 2){
					continue;
				}
				// TODO 基金暂不处理
				if("fu".equals(codeArr[0])){
					continue;
				}
				String allCode = StringUtil.ToString(codeArr[0] + codeArr[1]);
				String title = StringUtil.ToString(matcher.group(2));
				// 如果不存在股票代码， 则采集
				System.err.println(allCode + "=========" + title);
			}
        } catch (Exception e) {
            log.error("采集行业异常,", e);
        }
	}
}
