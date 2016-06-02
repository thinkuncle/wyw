package org.wyw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * 文件操作工具类
 * @author wanghaibo
 * @date 2015-3-24
 */
public class FileUtil {
	
	private static Logger log = Logger.getLogger(FileUtil.class);

	/**
	 * 判断文件是否存在
	 * @param dir
	 * @return
	 */
	public static boolean exists(String dir){
		File file = null;
		try {
			file = new File(dir);
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
			log.error("判断文件是否存在异常,", e);
		} finally {
		}
		return false;
	}
	
	
	
	/**
	 * 获取第一行数据
	 * @return
	 */
	public static String getFirstLine(String dir) {
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			file = new File(dir);
			if (file.exists() && file.isFile()) {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				String content = null;
				if ((content = br.readLine()) != null) {
					return content;
				}
			}
		} catch (Exception e) {
			log.error("读取文件第一行记录异常,", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
		return "";
	}
	
	
	
	/**
	 * 写文件，如果存在会删除\再新建
	 * @param dir
	 * @param content
	 * @return
	 */
	public static boolean write(String dir, String content){
		boolean flag = false;
		File file = null;
		FileWriter fw = null;
		try {
			file = new File(dir);
			if(file.exists()){
				file.delete();
			}
			// 创建文件
			file.createNewFile();
			fw = new FileWriter(file, true);
			if(content != null && !"".equals(content)){
				fw.write(content);
				flag = true;
			}
		} catch (Exception e) {
			log.error("写一个新文件异常,dir=" + dir, e);
		} finally {
			if (fw != null) {
				try {
					fw.flush();
					fw.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}
	/**
	 * 写文件，如果存在会删除\再新建
	 * @param dir
	 * @param content
	 * @return
	 */
	public static boolean newFile(String filePath, String fileName){
		boolean flag = false;
		String path = filePath + File.separator + fileName;
		File file = null;
		try {
			file = new File(path);
			if(file.exists()){
				file.delete();
			}
			// 创建文件
			file.createNewFile();
		} catch (Exception e) {
			log.error("写一个新文件异常,dir=" + path, e);
		} finally {
		}
		return flag;
	}
	/**
	 * 写文件，如果存在会删除\再新建
	 * @param dir
	 * @param content
	 * @return
	 */
	public static boolean newFile(String filePath){
		boolean flag = false;
		String path = filePath;
		File file = null;
		try {
			file = new File(path);
			if(file.exists()){
				file.delete();
			}
			// 创建文件
			file.createNewFile();
		} catch (Exception e) {
			log.error("写一个新文件异常,dir=" + path, e);
		} finally {
		}
		return flag;
	}
	
	/**
	 * 指定编码 追加写文件
	 * @param dir
	 * @param content
	 * @param codeSet
	 * @return
	 */
	public static boolean write(String dir, String content, String codeSet){
		boolean flag = false;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(dir, true); 
	        osw = new OutputStreamWriter(fos,  codeSet); 
	        osw.write(content); 
	        osw.flush();
		} catch (Exception e) {
			log.error("写一个新文件异常,", e);
		} finally {
			if(osw != null){
				try {
					osw.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}
	
	/**
	 * 写文件
	 * @param dir 文件位置
	 * @param content	文件内容
	 * @param codeSet	文件编码
	 * @param isAppend	是否追加写
	 * @return
	 */
	public static boolean write(String dir, String content, String codeSet, boolean isAppend){
		boolean flag = false;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(dir, isAppend); 
			osw = new OutputStreamWriter(fos,  codeSet); 
			osw.write(content); 
		} catch (Exception e) {
			log.error("写文件异常,", e);
		} finally {
			if(osw != null){
				try {
					osw.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}
	
	/**
	 * 删除文件
	 * @param dir
	 * @return
	 */
	public static boolean delFile(String filePath) {
		boolean flag = false;
		File file = null;
		try {
			file = new File(filePath);
			if (file.exists()) {
				flag = file.delete();
			}
		} catch (Exception e) {
			log.error("删除文件异常,", e);
		} finally {
		}
		return flag;
	}
	
	
	/**
	 * 删除目录
	 * @param dir
	 */
	public static void delDir(String dirName) {
		File dir = null;
		try {
			dir = new File(dirName);
			if (dir.exists()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			log.error("删除文件目录异常,", e);
		} finally {
		}
	}
	
	/**     * 递归删除目录下的所有文件及子目录下所有文件     
	 * * @param dir 将要删除的文件目录     
	 * @return boolean Returns "true" if all deletions were successful.     
	 */
	 private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		} // 目录此时为空，可以删除
		return dir.delete();
	}
	 
	/**
	 * 强制创建一个目录
	 * @param directory
	 * @throws IOException
	 */
	public static void forceMkdir(File directory) throws IOException {
		String message;
		if (directory.exists()) {
			if (!(directory.isFile()))
				return;
			message = "File " + directory + " exists and is "
					+ "not a directory. Unable to create directory.";
			throw new IOException(message);
		}
		if (!(directory.mkdirs())) {
			message = "Unable to create directory " + directory;
			throw new IOException(message);
		}
	}
	
	/**
	 * 创建目录
	 * @param realPath
	 */
	public static void mkdirs(String realPath){
		File file = new File(realPath);
		if (!file.exists()) {
			file.mkdirs();// 创建文件自定义子目录
		}
	}
	
	
	 /**
	  * 获取指定目录下的所有文件(不包括子文件夹)
	  * @param dirPath
	  * @return
	  */
	 public static ArrayList<File> getDirFiles(String dirPath) {
	     File path = new File(dirPath);
	     File[] fileArr = path.listFiles();
	     ArrayList<File> files = new ArrayList<File>();
	     for (File f : fileArr) {
	         if (f.isFile()) {
	             files.add(f);
	         }
	     }
	     return files;
	 }
	 
	 /**
	  * 获取指定目录下特定文件后缀名的文件列表(不包括子文件夹)
	  * @param dirPath 目录路径
	  * @param suffix 文件后缀
	  * @return
	  */
	 public static ArrayList<File> getDirFiles(String dirPath,
	         final String suffix) {
	     File path = new File(dirPath);
	     File[] fileArr = path.listFiles(new FilenameFilter() {
	         public boolean accept(File dir, String name) {
	             String lowerName = name.toLowerCase();
	             String lowerSuffix = suffix.toLowerCase();
	             if (lowerName.endsWith(lowerSuffix)) {
	                 return true;
	             }
	             return false;
	         }
	     });
	     ArrayList<File> files = new ArrayList<File>();
	     for (File f : fileArr) {
	         if (f.isFile()) {
	             files.add(f);
	         }
	     }
	     return files;
	 }
}
