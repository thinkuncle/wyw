package org.wyw.extend.freemarker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author hailang
 * @date 2009-7-9 上午09:55:43
 * @param templateName 模板文件名称 
 * @param root 数据模型根对象
 * @param templateEncoding 模板文件的编码方式
 */
public class FreeMarkerUtil {
	
	private static Logger log = Logger.getLogger(FreeMarkerUtil.class);
	private static String defEncode = "UTF-8"; 
	
	private static Configuration config = new Configuration();
	
	
	/**
	 * 初始化配置
	 * @param tempPath
	 */
	public static void initConfig(String tempPath, String encode){
		try {
			// 设置要解析的模板所在的目录，并加载模板文件
			config.setDirectoryForTemplateLoading(new File(tempPath));
			// 设置指定类所在目录下找模板
//			config.setClassForTemplateLoading(FreeMarkerUtil.class, tempPath);
			//设置包装器，并将对象包装为数据模型
			config.setObjectWrapper(new DefaultObjectWrapper());
			// 设置编码
			defEncode = encode;
			
		} catch (Exception e) {
			log.error("初始化模版配置异常,", e);
		}
	}
	
	/**
	 * 动态生成文件
	 * @param templateName 模板名称
	 * @param root 模板路径
	 * @param targetFile 输出目录
	 */
	public static void generate(String templateName, Map<?,?> root, String targetFile){
		try {
			//获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
			Template template = config.getTemplate(templateName,defEncode);
			//合并数据模型与模板
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), defEncode));  
			template.process(root, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			log.error(e);
		}catch (TemplateException e) {
			log.error(e);
		}
	} 
	
}
