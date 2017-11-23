package main.java.jdk.designPatterns.proxy.staticProxy;

public class ProxySubject extends Subject {
	private RealSubject rs;
	ProxySubject() {
		rs = new RealSubject();
	}
	
	@Override
	public void request() {
		preRequest();
		rs.request();
		afterRequest();
	}
	
	private void preRequest() {
		System.out.println("静态代理加入：请求方法执行前操作" + this.getClass().getName());
	}
	
	private void afterRequest() {
		System.out.println("静态代理加入：请求方法执行后操作" + this.getClass().getName());
	}
}
