package jdk.designPatterns.proxy.jdkdynamicProxy;

import java.lang.reflect.Proxy;

/**
 * 生成代理类$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 23:00
 */
public class LogProxyFactory {


    /**
     * 生成日志代理类
     * @param t
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(T t) {
        return (T) Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), new LogInvocationHandler<>(t));
    }
}
