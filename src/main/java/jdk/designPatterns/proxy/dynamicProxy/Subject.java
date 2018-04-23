package jdk.designPatterns.proxy.dynamicProxy;

public interface Subject {
    public void request();

    public void noProxyRequest();
}
