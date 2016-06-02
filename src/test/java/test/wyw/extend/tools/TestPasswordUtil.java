package test.wyw.extend.tools;

import org.junit.Test;
import org.wyw.extend.tools.PasswordUtil;

/**
 * 类描述：测试加密解密密码
 * @author:  wanghaibo
 * @date： 2016年5月9日
 * @version 1.0
 */
public class TestPasswordUtil {
	
	@Test
	public void test(){
		String user = "admin";
		String password = "123456";

		System.err.println("明文:" + user);
		System.err.println("密码:" + password);

		try {
			// 获得盐值
			byte[] salt = PasswordUtil.getStaticSalt();
			String ciphertext = PasswordUtil.encrypt(password, user, salt);
			System.err.println("密文:" + ciphertext);
			String plaintext = PasswordUtil.decrypt(ciphertext, user, salt);
			System.err.println("明文:" + plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

