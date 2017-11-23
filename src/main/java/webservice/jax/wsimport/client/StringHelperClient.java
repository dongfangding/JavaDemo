package main.java.webservice.jax.wsimport.client;

import org.junit.Test;

/**
 * 使用wsimport命令导入wsdl类 
 * wsimport -s d:\wsdl\ -p com.java.webservice.jax.wsimport.client http://localhost:7777/sh?wsdl
 * wsimport -s [目录] -p [指定包,(com.ddf)] [wsdllocation]
 * @author ddf 2016年9月22日上午10:19:15
 *
 */
public class StringHelperClient {
	
	@Test
	public void testClient() {
		// wsimport自动生成的包装接口的类，可以直接获得目标接口
		StringHelperImplService shis = new StringHelperImplService();
		// 获得目标接口
		StringHelper sh = shis.getStringHelperImplPort();
		System.out.println(sh.contact("hello", " world!"));
	}
}
