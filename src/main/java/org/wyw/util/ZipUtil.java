package org.wyw.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.helpers.LogLog;

/**
 * 文件压缩工具
 * @author wanghaibo
 *
 */
public class ZipUtil {

	/**
	 * 压缩文件
	 * @param AbsoluteFilePath
	 * @param name
	 * @return
	 */
	public static boolean compressFile(String AbsoluteFilePath, String name) {
		return compressFile(new File(AbsoluteFilePath), name);
	}

	/**
	 * 压缩文件
	 * @param file
	 * @param name
	 * @return
	 */
	public static boolean compressFile(File file, String name) {
		try {
			FileInputStream fin = new FileInputStream(file);
			FileOutputStream fout = new FileOutputStream(name);
			ZipOutputStream zout = new ZipOutputStream(fout);
			zout.putNextEntry(new ZipEntry(file.getName()));
			byte buf[] = new byte[1024];
			int num;
			while ((num = fin.read(buf)) != -1) {
				zout.write(buf, 0, num);
				zout.flush();
			}
			zout.close();
			fout.close();
			fin.close();
		} catch (IOException e) {
			LogLog.error("catch IOException while zipping file " + file.getName(), e);
			return false;
		}
		return true;
	}

	/**
	* @param path 要压缩的路径, 可以是目录, 也可以是文件
	* @param zo 压缩输出流
	* @param isRecursive 是否递归
	* @param isOutBlankDir 是否输出空目录, 要使输出空目录为true,同时baseFile不为null.
	* @throws IOException
	*/
	public static void compressDir(String path, String zipPath) {
		try {
			OutputStream os = new FileOutputStream(zipPath);
			BufferedOutputStream bs = new BufferedOutputStream(os);
			ZipOutputStream zo = new ZipOutputStream(bs);
			compressDir(path, new File(path), zo, true, true);
			zo.closeEntry();
			zo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param path 要压缩的路径, 可以是目录, 也可以是文件.
	 * @param basePath 如果path是目录,它一般为new File(path), 作用是:使输出的zip文件以此目录为根目录, 如果为null它只压缩文件, 不解压目录.
	 * @param zo 压缩输出流
	 * @param isRecursive 是否递归
	 * @param isOutBlankDir 是否输出空目录, 要使输出空目录为true,同时baseFile不为null.
	 * @throws IOException
	 */
	public static void compressDir(String path, File basePath, ZipOutputStream zo, boolean isRecursive,
			boolean isOutBlankDir) throws IOException {
		File inFile = new File(path);
		File[] files = new File[0];
		if (inFile.isDirectory()) { // 是目录
			files = inFile.listFiles();
		} else if (inFile.isFile()) { // 是文件
			files = new File[1];
			files[0] = inFile;
		}
		byte[] buf = new byte[1024];
		int len;
		for (int i = 0; i < files.length; i++) {
			String pathName = "";
			if (basePath != null) {
				if (basePath.isDirectory()) {
					pathName = files[i].getPath().substring(basePath.getPath().length() + 1);
				} else {// 文件
					pathName = files[i].getPath().substring(basePath.getParent().length() + 1);
				}
			} else {
				pathName = files[i].getName();
			}
			if (files[i].isDirectory()) {
				if (isOutBlankDir && basePath != null) {
					zo.putNextEntry(new ZipEntry(pathName + "/")); // 可以使空目录也放进去
				}
				if (isRecursive) { // 递归
					compressDir(files[i].getPath(), basePath, zo, isRecursive, isOutBlankDir);
				}
			} else {
				FileInputStream fin = new FileInputStream(files[i]);
				zo.putNextEntry(new ZipEntry(pathName));
				while ((len = fin.read(buf)) > 0) {
					zo.write(buf, 0, len);
				}
				fin.close();
			}
		}
	}

	/** 
	 * 解压到指定目录 
	 * @param zipPath 
	 * @param descDir 
	 * @author isea533 
	 */
	public static void unZipFiles(String zipPath, String descDir) throws IOException {
		unZipFiles(new File(zipPath), descDir);
	}

	/** 
	 * 解压文件到指定目录 
	 * @param zipFile 
	 * @param descDir 
	 * @author isea533 
	 */
	@SuppressWarnings("rawtypes")
	public static void unZipFiles(File zipFile, String descDir) throws IOException {
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = new ZipFile(zipFile);
		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
			;
			// 判断路径是否存在,不存在则创建文件路径
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if (!file.exists()) {
				file.mkdirs();
			}
			// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			if (new File(outPath).isDirectory()) {
				continue;
			}
			// 输出文件路径信息
			System.out.println(outPath);

			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
		System.out.println("******************解压完毕********************");
	}

}
