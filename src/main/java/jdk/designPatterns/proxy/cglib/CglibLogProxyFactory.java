package jdk.designPatterns.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

/**
 * $
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 23:20
 */
public class CglibLogProxyFactory {

    /**
     * 创建日志代理类
     * @param t
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> t) {
        Enhancer enhancer = new Enhancer();
        // 设置类加载器
        enhancer.setClassLoader(t.getClassLoader());
        // 设置被代理类
        enhancer.setSuperclass(t);
        // 设置方法拦截器
        enhancer.setCallback(new LogMethodInterceptor());
        // 创建代理类
        return (T) enhancer.create();
    }

    public static void main(String[] args) {
        AddCalcComponent addCalcComponent = new AddCalcComponent("1");
        System.out.println("代理前>>>");
        int result = addCalcComponent.add(1, 2, 3, 4);
        System.out.println("接收结果: " + result);

        System.out.println("\n\n代理后");
        AddCalcComponent proxy = CglibLogProxyFactory.getProxy(AddCalcComponent.class);
        int add = proxy.add(1, 2, 3, 4);
        System.out.println("代理后接收结果: " + add);
    }
}
