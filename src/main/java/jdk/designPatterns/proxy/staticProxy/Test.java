package main.java.jdk.designPatterns.proxy.staticProxy;

/**
 * 静态代理: 
 * 一个原类与一个代理类要一一对应。
 * 两者都实现共同的接口或继承相同的抽象类；
 * 只是在代理类中,实例化原类，在原类方法的前后添加新的逻辑。
 * @author DDf on 2016年8月11日下午2:05:32
 */
public class Test {
	public static void main(String[] args) {
		RealSubject rs = new RealSubject();
		rs.request();
		System.out.println("----------代理后---------");
		ProxySubject ps = new ProxySubject();
		ps.request();
	}
}
