package jdk.designPatterns.proxy.staticProxy;

public class RealSubject extends Subject {
    @Override
    public void request() {
        System.out.println("请求方法被执行！" + this.getClass().getName());
    }
}
