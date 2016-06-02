package test.wyw.util;

import java.io.IOException;

import org.junit.Test;
import org.wyw.util.ZipUtil;

public class TestZipUtil {
	private static String path = "D:\\test";
	private static String filepath = "D:\\test\\2.txt";
	private static String zippath = "D:\\zipfolder.zip";
	private static String zipfilepath = "D:\\file.zip";
	
	@Test
	public void test() {
		System.err.println("start...");
		System.err.println("压缩目录");
		ZipUtil.compressFile(filepath, zipfilepath);
		
		System.err.println("压缩文件夹目录");
		ZipUtil.compressDir(path, zippath);
		
		try {
			System.err.println("解压文件夹目录");
			ZipUtil.unZipFiles(zippath, "D:\\t");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("end...");
	}
	
	
}
