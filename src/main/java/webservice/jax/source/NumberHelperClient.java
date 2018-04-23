package webservice.jax.source;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * 访问发布的NumberHelper  webservice
 * 此种方式的缺点是，严重依赖接口，需要明确接口类，需要接口源码，不能跨平台。（源码可以用wsimport导入）
 * WSDL:
 * 1. types 用来定义访问的类型
 * 2. message: SOAP的方式来传输代表方法
 * 3. portType: 指定服务器的接口，并且通过operation绑定in和Out的消息,in表示参数，out标识返回值
 * 4. binding: 指定传递消息所使用的格式
 * 5. service: 指定服务器所发布的名称》
 * 6. targetNamespace命名空間为package的倒写。
 * 7. name 为接口实现类+Service
 *
 * @author ddf 2016年9月20日上午11:09:10
 */
public class NumberHelperClient {

    public static void main(String[] args) {
        try {
            // 指定访问的Url,发布webservice的wsdl
            URL url = new URL("http://localhost:9998/nh?wsdl");
            // 对应着wsdl文件的targetNamespace和name
            QName qName = new QName("http://source.jax.webservice.java.com/", "NumberHelperImplService");
            // 创建service
            Service service = Service.create(url, qName);
            // 获得发布的service接口(此行代码即对应着注释的严重缺陷)
            NumberHelper numberHelper = service.getPort(NumberHelper.class);
            System.out.println(numberHelper.add(4, 6));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
