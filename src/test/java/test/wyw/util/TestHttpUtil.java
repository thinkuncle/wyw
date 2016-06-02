package test.wyw.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;
import org.wyw.util.HttpUtil;

public class TestHttpUtil {

	@Test
	public void test() {
		String url = "https://api.weixin.qq.com/cgi-bin/token";
		String result = "";
		try {
			result = HttpUtil.doGet(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println(result);
	}


}
