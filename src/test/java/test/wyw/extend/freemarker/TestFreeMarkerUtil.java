package test.wyw.extend.freemarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wyw.extend.freemarker.FreeMarkerUtil;


/**
 * 类描述：
 * @author:  wanghaibo
 * @date： 2016年5月9日
 * @version 1.0
 */
public class TestFreeMarkerUtil {
	
	@Test
	public void test(){
		System.err.println("start...................");
		
		// 模板目录
		String ftlpath = this.getClass().getResource("").getPath();
		if(ftlpath.startsWith("/") || ftlpath.startsWith("\\")){
			ftlpath = ftlpath.substring(1);
		}
		// 输出目录
		String outpath = ftlpath + "index.html";
		System.err.println("ftlPath=" + ftlpath);
		System.err.println("outpath=" + outpath);
		// 加载配置文件
		FreeMarkerUtil.initConfig(ftlpath, "UTF-8");
		
		// 数据
		Map root = new HashMap();
		root.put("title", "资讯");
		List<String> list = new ArrayList<String>();
		list.add("测试A");
		list.add("测试B");
		list.add("测试C");
		list.add("测试D");
		list.add("测试E");
		root.put("menu", list);
		
		// 生成
		FreeMarkerUtil.generate("index.ftl", root, outpath);
		System.err.println("end.....................");
		
	}
}

