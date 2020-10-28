package jdk.designPatterns.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 日志拦截处理类$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 23:12
 */
public class LogMethodInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.printf("接收参数: %s\n", Arrays.toString(objects));
        long before = System.currentTimeMillis();

        Object result = methodProxy.invokeSuper(o, objects);
        long after = System.currentTimeMillis();

        System.out.printf("返回结果: %s, 共耗时%dms", result, after - before);
        return result;
    }
}
