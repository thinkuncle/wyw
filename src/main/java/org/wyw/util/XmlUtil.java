package org.wyw.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
 * 
 * @author zxb
 * @date 2010-10-9
 * @function 完成xml和对象之间的转换
 */
public class XmlUtil {

	/**将对象转换成xml字符串，使用UTF-8编码
	 * @param 转换的对象
	 * @param 是否需要缩进
	 * @return
	 */
	public static String beanToXml(Object obj, boolean isIndent) {
		return  beanToXml(obj, isIndent, "UTF-8");
	}
	
	
	/**将对象转换成xml字符串
	 * @param 转换的对象
	 * @param 是否需要缩进
	 * @param 编码
	 * @return
	 */
	public static String beanToXml(Object obj, boolean isIndent, String encode) {
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(obj.getClass());
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isIndent);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encode);

			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			
			String result = writer.toString();
			if(result != null){
				if(result.contains("&amp;") || result.contains("&lt;") || result.contains("&gt;")){
					result = result.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("将bean转换为xml失败");
		}
	}
	
	/**
	 * 将xml字符串转换成对象
	 * @param <T>
	 * @param xmlStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlToBean(String xmlStr, Class<T> clazz) throws Exception{
		T result;
		
		//过滤掉一些非法字符，这些非法字符是xml不允许的
		if(xmlStr !=  null){
			xmlStr = xmlStr.replaceAll("[\\x00-\\x08|\\x0b-\\x0c|\\x0e-\\x1f]", "");
		}
		
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		result = (T)unmarshaller.unmarshal(new StringReader(xmlStr));

		return result;
	}

}
