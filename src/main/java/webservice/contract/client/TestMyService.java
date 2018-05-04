package webservice.contract.client;

import org.junit.jupiter.api.Test;;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TestMyService {

    @Test
    public void testMyService() {
        try {
            MyServiceImplService msis = new MyServiceImplService();
            MyService ms = msis.getMyServiceImplPort();
            AddRequest addRequest = new AddRequest();
            addRequest.setNum1(10);
            addRequest.setNum2(20);
            System.out.println("add client:" + ms.add(addRequest)
                    .getAddResult());


            DivideRequest divideRequest = new DivideRequest();
            divideRequest.setNum1(30);
            divideRequest.setNum2(0);
            System.out.println("divide client:" + ms.divide(divideRequest).getDivideResult());
        } catch (SOAPFaultException e) {
            System.out.println(e.getFaultString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testMessage() {
        String ns = "http://www.example.org/myService/";
        try {
            URL url = new URL("http://localhost:8989/ms?wsdl");
            // 参数1对应这wsdl的命名空间,参数2为name
            QName qname = new QName(ns, "MyServiceImplService");
            // 1. 创建service
            Service service = Service.create(url, qname);

            // 参数2对应着service的port name
            QName portName = new QName(ns, "MyServiceImplPort");
            // 2. 创建dispatch
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
                    SOAPMessage.class, Service.Mode.MESSAGE);

            // 3. 创建MessageFactory->SOAPMessage->SOAPPart->SOAPEnvelope->(Body||Header)
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage requestMsg = mf.createMessage();
            SOAPPart part = requestMsg.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            SOAPBody body = envelope.getBody();
            SOAPHeader header = envelope.getHeader();
            if (header == null) {
                header = envelope.addHeader();
            }
            QName hn = new QName(ns, "authInfo");
            SOAPHeaderElement se = header.addHeaderElement(hn);
            se.addChildElement("userid").setValue("ddf");
            se.addChildElement("password").setValue("123456");
            // 4. 创建QName在SOAPBody中传递指定消息(login对应着wsdl中暴漏的方法)
            QName divide = new QName(ns, "divide");
            SOAPBodyElement ele = body.addBodyElement(divide);
            ele.addChildElement("num1").setValue("12");
            ele.addChildElement("num2").setValue("23");

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
}
