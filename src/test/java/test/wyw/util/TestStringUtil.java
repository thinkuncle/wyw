package test.wyw.util;

import org.junit.Test;
import org.wyw.util.StringUtil;

public class TestStringUtil {

	@Test
	public void test() {
		String enStr = "string test";
		String cnStr = "上帝是个无耻的老赌徒，他抛弃了我们！";
		String longStr = "死亡是一座永恒的灯塔，不管你驶向何方，最终都会朝它转向。一切都将逝去，只有死神永生";
		String htmlStr = "<ol><li>abc</li><li>张三</li><li>李四</li><li>王五</li><li>1900-01-01</li><li>tlxbf665@126.com</li></ol>";
		String emailStr = "<ol><li>tlxbf66@126.com</li><li>tommy.cui@seakingsoft.com</li><li>tonglian_liqun@163.com</li><li>tslhzsb@163.com</li></ol>";
		String jsonStr = "{'name': '张三'}";
		String emailPattern = "^([a-z0-9_\\.\\-\\+]+)@([\\da-z\\.\\-]+)\\.([a-z\\.]{2,6})$";
		String numPattern = "\\d";
		try{
			p("asciiToUTF8", StringUtil.asciiToUTF8(enStr));
			p("转换字符编码", "ChangeCharset", StringUtil.ChangeCharset(cnStr, "utf-8"));
			p("转义符转换", "ESCChange", StringUtil.ESCChange(enStr));
			p("toASCII格式", "ToASCII", StringUtil.ToASCII("toASCII格式"));
			
			p("回车符", "getEnter", StringUtil.getEnter());
			p("获取XX位的随机数", "getRandom", StringUtil.getRandom(4));
			p("MD5加密字符串", "MD5", StringUtil.MD5(enStr));
			p("数字批量正则替换", "regexReplace", StringUtil.regexReplace(emailStr, numPattern, "x"));
			p("首字母小写", "toFirstLowerCase", StringUtil.toFirstLowerCase("FIRSTLOTWERCASE"));
			p("首字母大写", "toFirstUpperCase", StringUtil.toFirstUpperCase("firstuppercase"));
			p("字母全部转换为小写", "toLowerCase", StringUtil.toLowerCase("LOTWERCASE"));
			p("字母全部转换为大写", "toUpperCase", StringUtil.toUpperCase("uppercase"));
			p("判断字符串是否为空", "isEmpty", StringUtil.isEmpty(""));
			p("生成唯一字符串(UUID)", "unique(uuid)", StringUtil.uniqueString());
			p("转换字符串", "ToString", StringUtil.ToString(null));
			p("转换字符串(去除值为null的空串)", "ToStringHandleNull", StringUtil.ToStringHandleNull("null"));
			p("截取字符串", "subStr", StringUtil.subStr(longStr, 15));
		}catch(Exception e){
			System.err.println("异常");
		}
	}
	
	public void p(String str){
		System.err.println(str);
	}
	
	public void p(String str1, String str2){
		System.err.println(str1 + "\t\t" + str2);
	}
	
	public void p(String str1, String str2, Object str3){
		System.err.println(str1 + "\t\t" + str2 + "\t\t" + str3);
	}
}
