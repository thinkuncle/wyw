package test.wyw.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.wyw.util.FileUtil;

public class TestFileUtil {
	
	private static String path = "D:\\test";
	private static String path2 = "D:\\test2";
	private static String path3 = "D:\\test\\aa\\bb\\cc";
//	private static String path4 = "D:\\test\\11\\22\\33";
	private static String filepath = "D:\\test\\2.txt";
//	private static String path2 = "D:\\test2";
	private static String newFile1 = "newfile.txt";
	private static String newfile2 = "D:\\test\\newfile2.txt";
	private static String newfile3 = "D:\\test\\newfile3.txt";
	private static String newfile4 = "D:\\test\\newfile4.txt";
	
	@Test
	public void test() {
		System.err.println("start...");
		System.err.println("新建目录===" + path + "===");
		FileUtil.mkdirs(path);
		System.err.println("判断文件或文件夹是否存在===" + FileUtil.exists(filepath));
		System.err.println("测试写入程序===" + filepath + "===" + FileUtil.write(filepath, "写入测试文件"));
		System.err.println("新建文件(存在则删除)===" + newFile1 + "===" + FileUtil.newFile(path, newFile1));
		System.err.println("新建文件2(存在则删除)===" + newfile2 + "===" + FileUtil.newFile(newfile2));
		System.err.println("追加写文件(指定编码)===" + newfile3 + "===" + FileUtil.write(newfile3, "测试文本", "utf-8"));
		System.err.println("追加写文件(追加)===" + newfile4 + "===" + FileUtil.write(newfile4, "测试文本", "utf-8", true));
		System.err.println("追加写文件(不追加)===" + newfile4 + "===" + FileUtil.write(newfile4, "追加写文件(是否追加)", "utf-8", false));
		System.err.println("删除文件===" + newfile2 + "===" + FileUtil.delFile(newfile2));
		System.err.println("删除目录===" + path2 + "===");
		FileUtil.delDir(path2);
		try {
			System.err.println("创建一个目录===" + path3 + "===");
			FileUtil.mkdirs(path3);
			System.err.println("强制创建一个目录===" + path3 + "===");
			FileUtil.forceMkdir(new File(path3));
			System.err.println("获取目录所有文件");
			ArrayList<File> files = FileUtil.getDirFiles(path);
			for(File f : files){
				System.err.println(f.getName());
			}
			System.err.println("获取目录所有文件(abc)");
			ArrayList<File> txtFiles = FileUtil.getDirFiles(path, "abc");
			for(File f : txtFiles){
				System.err.println(f.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("end...");
	}
	
	
}
