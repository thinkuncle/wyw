package org.wyw.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 * @author wanghaibo
 *
 */
public class StringUtil {
	
	public static final String US_ASCII = "US-ASCII";
	
	public static final String UTF_8 = "UTF-8";
	/**
	 * 小于号(<)
	 */
	public static final String SIGN_LESS = "<";
	/**
	 * 小于号(<)的转义符
	 */
	public static final String SIGN_LESS_2STR = "&lt;";
	/**
	 * 大于号(>)
	 */
	public static final String SIGN_GREATER = ">";
	/**
	 * 大于号(>)的转义符
	 */
	public static final String SIGN_GREATER_2STR = "&gt;";
	/**
	 * 和号(&)
	 */
	public static final String SIGN_AND = "&";
	/**
	 * 和号(&)的转义符
	 */
	public static final String SIGN_AND_2STR = "&amp;";
	
	
	/**
	 * MD5加密
	 * @param inStr 需要加密的字符串
	 * @return
	 */
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	
	/**
	 * 生成唯一字符串，即UUID
	 * @return UUID
	 */
	public static String uniqueString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 转义符转换
	 * @param str  转入串
	 * @return  转换后的串
	 */
	public static String ESCChange(String str){
		if(str==null || str.trim().equals("") || "null".equals(str))return "";
		str = str.replace(SIGN_AND, SIGN_AND_2STR);
		str = str.replace(SIGN_GREATER, SIGN_GREATER_2STR);
		str = str.replace(SIGN_LESS, SIGN_LESS_2STR);
		str = str.replace("\"", "&quot;");
		return str;
	}
	
	/**
	 * 获得count位随机数
	 * @param count
	 * @return
	 */
	public static String getRandom(int count){
		StringBuffer re = new StringBuffer();
		for(int i=0;i<count;i++){
			Random r = new Random();
			re.append(r.nextInt(10));
		}
		return re.toString();
	}
	
	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String toFirstLowerCase(String str){
		String lowerStr = "";
		if(str != null){
			if(str.length() > 1){
			 lowerStr = String.valueOf(Character.toLowerCase(str.charAt(0)))+str.substring(1);
			}else if(str.length() == 1){
				lowerStr = String.valueOf(Character.toLowerCase(str.charAt(0)));
			}
		}
		return lowerStr;
	}
	
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String toFirstUpperCase(String str){
		String lowerStr = "";
		if(str != null){
			if(str.length() > 1){
				lowerStr = String.valueOf(Character.toUpperCase(str.charAt(0)))+str.substring(1);
			}else if(str.length() == 1){
				lowerStr = String.valueOf(Character.toUpperCase(str.charAt(0)));
			}
		}
		return lowerStr;
	}
	 
	 /**
	 * 将字符串转换成int类型
	 * @param str
	 * @return
	 */
	public static int ToInt(String str) {
		return str == null ? 0 : "".equals(str) ? 0 : Integer.parseInt(str);
	}
	public static int ToInt(Object obj) {
		return obj == null ? 0 : "".equals(obj.toString().trim()) ? 0 : Integer.parseInt(obj.toString());
	}
	public static int ToInt(String str, int defValue) {
		return str == null ? defValue : "".equals(str) ? defValue : Integer.parseInt(str);
	}
	
	public static Long ToLong(String str) {
		return str == null ? 0L : "".equals(str) ? 0L : Long.parseLong(str);
	}
	public static Double ToDouble(Double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(d));
	}
	public static Double ToDouble(Double d, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return Double.valueOf(df.format(d));
	}
	public static String toLowerCase(String str){
		String lowerStr = "";
		if(str != null){
			lowerStr = str.toLowerCase();
		}
		return lowerStr;
	}
	public static String toUpperCase(String str){
		String lowerStr = "";
		if(str != null){
			lowerStr = str.toUpperCase();
		}
		return lowerStr;
	}
	
	public static String ToString(String str) {
		return ToString(str, "");
	}
	
	public static String ToString(Object obj) {
		return ToString(obj, "");
	}
	
	public static String ToString(String str, String delValue) {
		return str == null ? delValue : "".equals(str) ? delValue : str;
	}
	
	/**
	 * 对于空对象返回空字符串""
	 * @param str
	 * @return
	 */
	public static String ToStringHandleNull(String str) {
		return ToString(str) == "null" ? "" : ToString(str);
	}
	
	public static String ToString(Object obj, String delValue) {
		return obj == null ? delValue : "".equals(obj.toString()) ? delValue : obj.toString();
	}
	
	public static String getEnter(){
		String enterStr = "\r\n";
		if(System.getProperties().getProperty("file.separator").equals("/")){
			enterStr = "\n";
		}
		return enterStr;
	}
	
	/**
	 * 正则替换
	 * @param str
	 * @param regex
	 * @param repValue
	 * @return
	 */
	public static String regexReplace(String str, String regex, String repValue) {
		Pattern p = null;
		Matcher m = null;
		String value = null;

		// 去掉:标签及其之间的内容   
		p = Pattern.compile(regex);
		m = p.matcher(str);
		String temp = str;
		//下面的while循环式进行循环匹配替换，把找到的所有   
		//符合匹配规则的字串都替换为你想替换的内容   
		while (m.find()) {
			value = m.group(0);
			temp = temp.replace(value, repValue);
		}
		return temp;
	}
	
	public final static String ToASCII(String sStr) throws UnsupportedEncodingException {
		return ChangeCharset(sStr, US_ASCII);
	}
	public final static String asciiToUTF8(String sStr) throws UnsupportedEncodingException {
		return new String(sStr.getBytes(US_ASCII), UTF_8);
	}
	
	/**
	 * 转换字符编码
	 * @param sStr
	 * @param sNewCharset
	 * @throws UnsupportedEncodingException
	 */
	public final static String ChangeCharset(String sStr, String sNewCharset) throws UnsupportedEncodingException {
		byte[] aBits = sStr.getBytes();
		return new String(aBits, sNewCharset);
	}
	
	/**
	 * 判断是否为空
	 * @param url
	 * @return
	 */
	public final static boolean isEmpty(String str){
		if(str == null || "".equals(str) || "".equals(str.trim())){
			return true;
		}
		return false;
	}
	/**
	 * 判断是否为非空
	 * @param url
	 * @return
	 */
	public final static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	/**
	 * 截取字符串 获取摘要所要的长度
	 */
	public final static String subStr(String srcStr, int length){
		srcStr = ToString(srcStr);
		return srcStr.length() > length ? srcStr.substring(0, length) + "..." 
				: srcStr;
	}
	
	
}
