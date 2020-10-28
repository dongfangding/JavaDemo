package jdk.designPatterns.proxy.staticProxy;

/**
 * 原始接口实现
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 22:35
 */
public class SimpleHelloService implements HelloService {
    /**
     * 接口方法
     *
     * @param name
     */
    @Override
    public void sayHello(String name) {
        System.out.println("hello " + name);
    }
}
