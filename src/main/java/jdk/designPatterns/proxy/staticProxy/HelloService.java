package jdk.designPatterns.proxy.staticProxy;

/**
 * 静态代理要求必须基于接口$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 22:34
 */
public interface HelloService {

    /**
     * 接口方法
     * @param name
     */
    void sayHello(String name);
}
