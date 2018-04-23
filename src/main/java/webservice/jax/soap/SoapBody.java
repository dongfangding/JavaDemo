package webservice.jax.soap;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 * 如何封装SOAPbody
 *
 * @author ddf 2016年9月21日下午2:50:55
 */
public class SoapBody {
    public static void main(String args[]) {
        try {
            // 1. 创建消息工厂
            MessageFactory mf = MessageFactory.newInstance();
            // 2. 创建一个SOAPMessage
            SOAPMessage message = mf.createMessage();
            // 3. 创建SOAPPart
            SOAPPart part = message.getSOAPPart();
            // 4. 创建一个信封
            SOAPEnvelope envelope = part.getEnvelope();
            // 5. 通过SOAPEnvelope可以获得Body和Header
            SOAPBody body = envelope.getBody();
            // 对应着wsdl中的<ns:add xmlns="com.ddf.jax.soapbody"/>
            QName qname = new QName("com.ddf.jax.soapbody", "add", "ns");
            // 设置节点元素
            SOAPElement ele = body.addBodyElement(qname);
            ele.addChildElement("a").setValue("12");
            ele.addChildElement("b").setValue("20");
            // 打印消息
            message.writeTo(System.out);
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
