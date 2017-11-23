package main.java.webservice.jax.source;

import javax.jws.WebService;

/**
 * SIB(Service Implemention Bean)此类的概念
 * endpointInterface用来指定该webservice实现的接口
 * @author ddf 2016年9月20日上午10:59:39
 */
@WebService(endpointInterface="com.java.webservice.jax.source.NumberHelper")
public class NumberHelperImpl implements NumberHelper {

	@Override
	public int add(int a, int b) {
		System.out.println(a + "+" + b + "=:" + (a + b));
		return a + b;
	}

	@Override
	public int jian(int a, int b) {
		System.out.println(a + "-" + b + "=" + (a - b));
		return a - b;
	}

}
