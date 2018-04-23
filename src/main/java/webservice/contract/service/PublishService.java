package webservice.contract.service;

import javax.xml.ws.Endpoint;

/**
 * 开发流程
 * 1. 先写一个schema和wsdl文件（META-INF文件夹下）
 * 2. 根据这个文件使用wsimport生成服务端代码，目录为本地wsdl目录，如果是客户端wsdl需要用指定的发布的Location导入
 * 3. 编写接口实现类（在类上指定wsdlLocation）
 * 4. 发布服务
 *
 * @author ddf 2016年9月27日下午5:19:03
 */
public class PublishService {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8989/ms", new MyServiceImpl());
        System.out.println("publish over!");
    }

}
