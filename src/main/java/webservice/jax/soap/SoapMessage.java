package webservice.jax.soap;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import webservice.entity.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 手动模拟发送SOAP消息，本类依赖com.java.webservice.jax.source.NumberHelperService的发布
 * 基于MESSAGE的方式处理
 *
 * @author ddf 2016年9月21日下午3:51:45
 */
public class SoapMessage {

    @Test
    public void testNumberHelper() {
        String ns = "http://source.jax.ddf.com/";
        try {
            URL url = new URL("http://localhost:9998/nh?wsdl");
            // 参数1对应这wsdl的命名空间,参数2为name
            QName qname = new QName(ns, "NumberHelperImplService");
            // 1. 创建service
            Service service = Service.create(url, qname);

            // 参数2对应着service的port name
            QName portName = new QName(ns, "NumberHelperImplPort");
            // 2. 创建dispatch
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
                    SOAPMessage.class, Service.Mode.MESSAGE);

            // 3. 创建MessageFactory->SOAPMessage->SOAPPart->SOAPEnvelope->(Body||Header)
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage requestMsg = mf.createMessage();
            SOAPPart part = requestMsg.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            SOAPBody body = envelope.getBody();

            // 4. 创建QName在SOAPBody中传递指定消息(add对应着wsdl中暴漏的方法)
            QName addName = new QName(ns, "add");
            SOAPElement addEle = body.addBodyElement(addName);
            // a对应着方法add在wsdl中对应的方法参数名，添加请求参数，如果没有，可以不用添加
            addEle.addChildElement("a").setValue("12");
            addEle.addChildElement("b").setValue("13");
            System.out.println("---------------soap request-----------");
            requestMsg.writeTo(System.out);

            // 5. 通过dispatch传递请求消息，并接收返回值
            SOAPMessage responseMsg = dispatch.invoke(requestMsg);
            System.out.println("\n\n-----------soap response-------------");
            responseMsg.writeTo(System.out);

            // 6. 将返回结果转换为Document
            Document doc = responseMsg.getSOAPPart().getEnvelope().getBody()
                    .extractContentAsDocument();
            System.out.println("\n-----------Document------------");
            System.out.println(doc.getElementsByTagName("addResult")
                    .item(0).getNodeValue());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 测试UserService的list方法
     */
    @Test
    public void testUserList() {
        String ns = "http://soap.jax.webservice.java.com/";
        try {
            URL url = new URL("http://localhost:8889/us?wsdl");
            // 参数1对应这wsdl的命名空间,参数2为name
            QName qname = new QName(ns, "UserServiceImplService");
            // 1. 创建service
            Service service = Service.create(url, qname);

            // 参数2对应着service的port name
            QName portName = new QName(ns, "UserServiceImplPort");
            // 2. 创建dispatch
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
                    SOAPMessage.class, Service.Mode.MESSAGE);

            // 3. 创建MessageFactory->SOAPMessage->SOAPPart->SOAPEnvelope->(Body||Header)
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage requestMsg = mf.createMessage();
            SOAPPart part = requestMsg.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            SOAPBody body = envelope.getBody();

            // 4. 创建QName在SOAPBody中传递指定消息(list对应着wsdl中暴漏的方法)
			/*QName addName = new QName(ns, "list");
			body.addBodyElement(addName);
			System.out.println("---------------soap request-----------");
			requestMsg.writeTo(System.out);*/

            // 5. 通过dispatch传递请求消息，并接收返回值
            SOAPMessage responseMsg = dispatch.invoke(requestMsg);
            System.out.println("\n\n-----------soap response-------------");
            responseMsg.writeTo(System.out);

            // 6. 将返回结果转换为Document
            Document doc = responseMsg.getSOAPPart().getEnvelope().getBody()
                    .extractContentAsDocument();
            System.out.println("\n-----------Document------------");
            NodeList nodeList = doc.getElementsByTagName("user");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                JAXBContext jct = JAXBContext.newInstance(User.class);
                Unmarshaller um = jct.createUnmarshaller();
                User user = (User) um.unmarshal(node);
                System.out.println(user.toString());
                System.out.println();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 测试异常(此方法有问题，未运行起来，找不到原因)
     * 异常在wsdl中的operation中的fault节点中
     */
    @Test
    public void testLoginException() {
        String ns = "http://soap.jax.webservice.java.com/";
        try {
            URL url = new URL("http://localhost:8889/us?wsdl");
            // 参数1对应这wsdl的命名空间,参数2为name
            QName qname = new QName(ns, "UserServiceImplService");
            // 1. 创建service
            Service service = Service.create(url, qname);

            // 参数2对应着service的port name
            QName portName = new QName(ns, "UserServiceImplPort");
            // 2. 创建dispatch
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
                    SOAPMessage.class, Service.Mode.MESSAGE);

            // 3. 创建MessageFactory->SOAPMessage->SOAPPart->SOAPEnvelope->(Body||Header)
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage requestMsg = mf.createMessage();
            SOAPPart part = requestMsg.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            SOAPBody body = envelope.getBody();

            // 4. 创建QName在SOAPBody中传递指定消息(login对应着wsdl中暴漏的方法)
            QName loginName = new QName(ns, "login");
            SOAPBodyElement ele = body.addBodyElement(loginName);
            ele.addChildElement("username").setValue("fewf");
            ele.addChildElement("password").setValue("3232");

            System.out.println("---------------soap request-----------");
            requestMsg.writeTo(System.out);

            // 5. 通过dispatch传递请求消息，并接收返回值
            SOAPMessage responseMsg = dispatch.invoke(requestMsg);
            System.out.println("\n\n-----------soap response-------------");
            responseMsg.writeTo(System.out);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        } catch (SOAPFaultException e) {
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testWeather() {
        String ns = "http://WebXml.com.cn/";
        try {
            URL url = new URL("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl");
            // 参数1对应这wsdl的命名空间,参数2为发布的service的name属性
            QName qname = new QName(ns, "WeatherWebService");
            // 1. 创建service
            Service service = Service.create(url, qname);

            // 参数2对应着service的port name
            QName portName = new QName(ns, "WeatherWebServiceSoap");
            // 2. 创建dispatch
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
                    SOAPMessage.class, Service.Mode.MESSAGE);

            // 3. 创建MessageFactory->SOAPMessage->SOAPPart->SOAPEnvelope->(Body||Header)
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage requestMsg = mf.createMessage();
            SOAPPart part = requestMsg.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            SOAPBody body = envelope.getBody();
			
			/*// 4. 创建QName在SOAPBody中传递指定消息(add对应着wsdl中暴漏的方法)
			QName addName = new QName(ns, "getSupportCity");
			SOAPElement addEle = body.addBodyElement(addName);
			// a对应着方法add在wsdl中对应的方法参数名，添加请求参数，如果没有，可以不用添加
			addEle.addChildElement("byProvinceName").setValue("anhui");*/
            System.out.println("---------------soap request-----------");
            requestMsg.writeTo(System.out);

            // 5. 通过dispatch传递请求消息，并接收返回值
            SOAPMessage responseMsg = dispatch.invoke(requestMsg);
            System.out.println("\n\n-----------soap response-------------");
            responseMsg.writeTo(System.out);
			/*
			// 6. 将返回结果转换为Document
			Document doc = responseMsg.getSOAPPart().getEnvelope().getBody()
					.extractContentAsDocument();
			System.out.println("\n-----------Document------------");
			System.out.println(doc.getElementsByTagName("addResult")
					.item(0).getNodeValue());*/
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
