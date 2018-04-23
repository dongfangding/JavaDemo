package jdk.designPatterns.proxy.dynamicProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理：
 * Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)做了以下几件事.
 * （1）根据参数loader和interfaces调用方法 getProxyClass(loader, interfaces)创建代理类$Proxy0.$Proxy0类 实现了interfaces的接口,并继承了Proxy类.
 * （2）实例化$Proxy0并在构造方法中把DynamicSubject传过去,接着$Proxy0调用父类Proxy的构造器,为h赋值
 * (3)接着把得到的$Proxy0实例强制转换成Subject，并将引用赋给subject。当执行subject.request()方法时，
 * 就调用了$Proxy0类中的request()方法，进而调用父类Proxy中的h的invoke()方法.即InvocationHandler.invoke()
 *
 * @author DDf on 2016年8月11日下午3:11:35
 */
public class Test {
    public static void main(String[] args) {
        Subject rs = new RealSubject();
        InvocationHandler handler = new DynamicSubject(rs);
        Subject subject = ((Subject) Proxy.newProxyInstance(rs.getClass().getClassLoader(),
                rs.getClass().getInterfaces(), handler));
        //这里可以通过运行结果证明subject是Proxy的一个实例，这个实例实现了Subject接口
        System.out.println("代理类是否继承自Proxy: " + (subject instanceof Proxy));
        //这里可以看出subject的Class类是$Proxy0,这个$Proxy0类继承了Proxy，实现了Subject接口  
        System.out.println("subject的Class类是：" + subject.getClass().toString());
        System.out.print("subject中的属性有：");

        Field[] field = subject.getClass().getDeclaredFields();
        for (Field f : field) {
            System.out.print(f.getName() + ", ");
        }

        System.out.print("\n" + "subject中的方法有：");

        Method[] method = subject.getClass().getDeclaredMethods();

        for (Method m : method) {
            System.out.print(m.getName() + ", ");
        }

        System.out.println("\n" + "subject的父类是：" + subject.getClass().getSuperclass());

        System.out.println("\n" + "subject实现的接口是：");

        Class<?>[] interfaces = subject.getClass().getInterfaces();

        for (Class<?> i : interfaces) {
            System.out.print(i.getName() + ", ");
        }

        System.out.println("\n-------------------");
        subject.request();
        // 所有的方法都会被代理
        subject.noProxyRequest();
        // 如果不使用被代理的方法，使用被代理类调用该方法
        rs.noProxyRequest();
    }

}
