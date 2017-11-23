package main.java.jdk.designPatterns.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicSubject implements InvocationHandler {
	private Object obj;
	DynamicSubject(Object obj) {
		this.obj = obj;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object resultProxy = new Object();
		System.out.println("被代理方法：" + method);
		preHandler();
		resultProxy = method.invoke(obj, args);
		afterHandler();
		return resultProxy;
	}
	
	private void preHandler() {
		System.out.println("动态代理加入后请求前操作！");
	}
	
	private void afterHandler() {
		System.out.println("动态代理加入后请求后操作！");
	}
}
