package test.wyw.demo;

import org.junit.Test;

/**
 * 类描述：
 * @author:  wanghaibo
 * @date： 2016年5月9日
 * @version 1.0
 */
public class SystemParameter {
	
	@Test
	public void test(){
		p("java.vendor");
		p("java.vendor.url");
		p("java.home");
		p("java.vm.specification.version");
		p("java.vm.specification.vendor");
		p("java.vm.specification.name");
		
		p("java.vm.version");
		p("java.vm.vendor");
		p("java.vm.name");
		p("java.specification.version");
		p("java.specification.vendor");
		p("java.specification.name");
		p("java.class.version");
		
		p("java.class.path");
		p("java.library.path");
		p("java.io.tmpdir");
		p("java.compiler");
		p("java.ext.dirs");
		p("os.name");
		p("os.arch");
		p("os.version");
		p("file.separator");
		p("path.separator");
		p("line.separator");
		p("user.name");
		p("user.home");
		p("user.dir");
	}
	
	private void p(String str){
		System.err.println(str + "========" + System.getProperty(str));
	}
}

