package jdk.designPatterns.proxy.dynamicProxy;

public class RealSubject implements Subject {
    @Override
    public void request() {
        System.out.println("请求方法被执行" + this.getClass().getName());
    }

    @Override
    public void noProxyRequest() {
        System.out.println("没有加入代理拦截操作！");
    }

}
