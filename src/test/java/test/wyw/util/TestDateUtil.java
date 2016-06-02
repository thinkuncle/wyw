package test.wyw.util;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;
import org.wyw.util.DateUtil;

public class TestDateUtil {

	@Test
	public void test() {
		p("获取当前时间", "formatDate", DateUtil.formatDate());
		p("获取当前时间格式(yyyy-MM-dd)", "getDataString", DateUtil.getDataString(DateUtil.date_sdf));
		p("获取日期的毫秒数", "getMillis", DateUtil.getMillis(new Date()));
		p("日期转换为字符串", "date2Str", DateUtil.date2Str(new Date(), DateUtil.datetimeFormat));
		p("指定日期按指定格式显示", "formatDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
		p("指定日期的默认显示", "formatShortTime", DateUtil.formatShortTime());
		String src = "2003-11-19 11:20:20"; 
		String pattern = "yyyy-MM-dd HH";
		int amount = 4;
		try {
			p("格式化原数据", "formatAddDate", DateUtil.formatAddDate(src, pattern, amount));
		} catch (ParseException e) {
			e.printStackTrace();
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
