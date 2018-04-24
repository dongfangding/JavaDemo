package webservice.jax.soap;

import org.junit.Test;
import org.w3c.dom.NodeList;
import webservice.entity.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

/**
 * 基于PAYLOAD的方式处理
 *
 * @author ddf 2016年9月22日上午10:24:22
 */
public class SoapPayload {
    String ns = "http://soap.jax.webservice.java.com/";
    String wsdlLocation = "http://localhost:8889/us?wsdl";

    /**
     * 测试添加User。在body中添加请求参数
     */
    @Test
    public void testAddUser() {
        try {
            //1、创建服务(Service)
            URL url = new URL(wsdlLocation);
            QName sname = new QName(ns, "UserServiceImplService");
            Service service = Service.create(url, sname);

            //2、创建Dispatch(通过源数据的方式传递)
            Dispatch<Source> dispatch = service.createDispatch(new QName(ns, "UserServiceImplPort"),
                    Source.class, Service.Mode.PAYLOAD);
            //3、根据用户对象创建相应的xml
            User user = new User(3, "zs", "张三", "11111");
            JAXBContext ctx = JAXBContext.newInstance(User.class);
            Marshaller mar = ctx.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();
            mar.marshal(user, writer);

            //4、封装相应的part addUser
            String payload = "<nn:addUser xmlns:nn=\"" + ns + "\">" + writer.toString() + "</nn:addUser>";
            System.out.println(payload);
            StreamSource rs = new StreamSource(new StringReader(payload));

            //5、通过dispatch传递payload
            Source response = (Source) dispatch.invoke(rs);

            //6、将Source转化为DOM进行操作，使用Transform对象转换
            Transformer tran = TransformerFactory.newInstance().newTransformer();
            DOMResult result = new DOMResult();
            tran.transform(response, result);

            //7、处理相应信息(通过xpath处理)
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList) xpath.evaluate("//user", result.getNode(), XPathConstants.NODESET);
            User ru = (User) ctx.createUnmarshaller().unmarshal(nl.item(0));
            System.out.println(ru.getNickname());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
