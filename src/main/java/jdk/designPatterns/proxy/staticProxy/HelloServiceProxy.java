package jdk.designPatterns.proxy.staticProxy;

/**
 * 创建hello service的代理工厂$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 22:36
 */
public class HelloServiceProxy implements HelloService {

    /**
     * 被代理的类
     */
    private final HelloService target;

    public HelloServiceProxy(final HelloService helloService) {
        this.target = helloService;
    }


    /**
     * 接口方法
     *
     * @param name
     */
    @Override
    public void sayHello(String name) {
        System.out.println("sayHello方法被代理，开始执行>>>>>");
        target.sayHello(name);
        System.out.println("sayHello方法被代理，执行结束>>>>>");
    }

    public static void main(String[] args) {
        HelloService helloService = new SimpleHelloService();
        String name = "ddf";
        System.out.println("被代理前>>>>");
        helloService.sayHello(name);


        System.out.println("\n");
        System.out.println("被代理后>>>");
        HelloServiceProxy helloServiceProxy = new HelloServiceProxy(helloService);
        helloServiceProxy.sayHello(name);
    }
}
