package jdk.designPatterns.proxy.jdkdynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 代理处理类$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 22:50
 */
public class LogInvocationHandler<T> implements InvocationHandler {

    /**
     * 被代理的类，这个才是原始类
     */
    private final T target;

    public LogInvocationHandler(T target) {
        this.target = target;
    }

    /**
     * 代理处理逻辑
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.printf("接收参数: %s\n", Arrays.toString(args));
        long before = System.currentTimeMillis();

        Object result = method.invoke(target, args);
        long after = System.currentTimeMillis();

        System.out.printf("返回结果: %s, 共耗时%dms", result, after - before);
        return result;
    }

    public static void main(String[] args) {
        String id = "1";
        CalcService calcService = new AddCalcService(id);
        System.out.println("代理前: >>>>");
        int calc = calcService.calc(1, 2, 3, 4);
        System.out.println("接收返回结果: " + calc);

        CalcService proxy = LogProxyFactory.getProxy(calcService);
        System.out.println("\n\n代理后>>>");
        proxy.calc(1, 2,3, 4);
    }
}
