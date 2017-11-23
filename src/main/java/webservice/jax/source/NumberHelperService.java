package main.java.webservice.jax.source;
import javax.xml.ws.Endpoint;

/**
 * 使用jax-ws(java api xml - webservice)发布该webservice
 * @author ddf 2016年9月20日上午11:02:57
 *
 */
public class NumberHelperService {
	public static void main(String[] args) {
		// 指定发布后的访问地址
		String address = "http://localhost:9998/nh";
		// String address = "http://192.168.0.113:9998/nh";
		// 发布webservice的类
		Endpoint.publish(address, new NumberHelperImpl());
		System.out.println("over");
	}

}
