package jdk.XML;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class XMLUtil {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);

    /**
     * 对报文进行schema验证，并记录信息
     *
     * @param xmlFileLocation
     * @param xsdFileLocation
     * @param sbl
     * @return
     */
    public static boolean validate(String xmlFileLocation, String xsdFileLocation, StringBuilder sbl) {
        try {
            //创建默认的XML错误处理器
            XMLErrorHandler errorHandler = new XMLErrorHandler();
            //获取基于 SAX 的解析器的实例
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //解析器在解析时验证 XML 内容。
            factory.setValidating(true);
            //指定由此代码生成的解析器将提供对 XML 名称空间的支持。
            factory.setNamespaceAware(true);
            //使用当前配置的工厂参数创建 SAXParser 的一个新实例。
            SAXParser parser = factory.newSAXParser();
            //创建一个读取工具
            SAXReader xmlReader = new SAXReader();
            //获取要校验xml文档实例
            Document xmlDocument = (Document) xmlReader.read(new File(xmlFileLocation));
            //设置 XMLReader 的基础实现中的特定属性。核心功能和属性列表可以在 [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。
            parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaSource",
                    "file:" + xsdFileLocation);
            //创建一个SAXValidator校验工具，并设置校验工具的属性
            SAXValidator validator = new SAXValidator(parser.getXMLReader());
            //设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
            validator.setErrorHandler(errorHandler);
            //校验
            validator.validate(xmlDocument);

            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
            //如果错误信息不为空，说明校验失败，打印错误信息
            if (errorHandler.getErrors().hasContent()) {
                log.error("XML文件通过XSD文件校验失败！");
                writer.write(errorHandler.getErrors());
                sbl.append("XML文件通过XSD文件校验失败！");
                sbl.append(errorHandler.getErrors().getStringValue());
                return false;
            } else {
                log.info("Good! XML文件通过XSD文件校验成功！");
                sbl.append("Good! XML文件通过XSD文件校验成功！");
                return true;
            }
        } catch (Exception ex) {
            log.error("XML文件: " + xmlFileLocation + " 通过XSD文件:" + xsdFileLocation + "检验失败。\n原因： " + ex.getMessage());
            sbl.append("XML文件: " + xmlFileLocation + " 通过XSD文件:" + xsdFileLocation + "检验失败。\n原因： " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 创建自定义格式标准化的文档
     * add by ddf 20160627
     *
     * @param ref
     * @param documentName
     * @param senderCode
     * @param recCode
     * @return
     */
    public static Document createDocumentRootHeader(String ref, String documentName, String senderCode, String recCode) {
        Document document = DocumentHelper.createDocument();
        Element root = null;
        if (documentName.equals("E_EXP_CANCEL")) {
            root = document.addElement("Root", "http://com.aec.exp/expCancel");
        } else if (documentName.equals("E_EXP_RTN")) {
            root = document.addElement("Root", "http://com.aec.exp/expReturn");
        } else {
            root = document.addElement("Root");
        }
        Element header = root.addElement("Header");
        Element messageRefEle = header.addElement("MessageReferenceNumber");
        messageRefEle.setText(ref);
        Element documentNameEle = header.addElement("DocumentName");
        documentNameEle.setText(documentName);
        Element senderCodeEle = header.addElement("SenderCode");
        senderCodeEle.setText(senderCode);
        Element ReceiverCode = header.addElement("ReceiverCode");
        ReceiverCode.setText(recCode);
        Element MessageSendingDateTime = header.addElement("MessageSendingDateTime");
        MessageSendingDateTime.setText(dateFormat.format(new Date()));
        document.setXMLEncoding("UTF-8");
        return document;
    }

    /**
     * 格式化xml文档并且写入到文件
     *
     * @param document
     * @param path
     * @author ddf 20160627
     */
    public static void writeFormatXml(Document document, String path) {
        FileWriter fw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        OutputFormat xmlFormat = new OutputFormat();
        xmlFormat.setEncoding("UTF-8");
        // 设置换行
        xmlFormat.setNewlines(true);
        // 生成缩进
        xmlFormat.setIndent(true);
        // 使用4个空格进行缩进, 可以兼容文本编辑器
        xmlFormat.setIndent("    ");

        XMLWriter xmlWriter = new XMLWriter(fw, xmlFormat);
        try {
            xmlWriter.write(document);
            System.out.println("报文内容：" + document.asXML());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过字符串的方式获得xml根节点
     *
     * @param xml
     * @return
     * @author ddf 20160601
     */
    public static Element getRootByDom4j(String xml) {
        Element root = null;
        if (xml != null && !"".equals(xml)) {
            try {
                Document document = DocumentHelper.parseText(xml);
                root = document.getRootElement();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    /**
     * 通过输入流的方式获得xml根节点，并指定编码
     *
     * @param file
     * @param charset
     * @return
     * @author ddf 20160601
     */
    public static Element getRootByDom4jFile(String file, String charset) {
        Element root = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(br);
            root = document.getRootElement();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return root;
    }


    /**
     * 将当前节点的直接子节点的数据放到map中，循环使用时不能装进同一个map,只能取直接子节点
     *
     * @param element
     * @param resultMap
     * @author ddf 20160601
     */
    public static void getSingleMap(Element element, Map<String, String> resultMap) {
        if (element != null && element.nodeCount() > 0) {
            for (int i = 0; i < element.nodeCount(); i++) {
                Node node = element.node(i);
                resultMap.put(node.getName(), node.getStringValue());
            }
        }
    }

    /**
     * 拼接报文头
     *
     * @param messageReferenceNumber 报文参考号（判断什么报文）
     * @param documentName           单证名称（接收方判断为什么报文）
     * @param senderCode             发送方代码
     * @param receiverCode           接收方代码
     * @param sendDate               发送时间
     * @return
     */
    public static String getHeaderStr(String messageReferenceNumber, String documentName,
                                      String senderCode, String receiverCode, String sendDate) {
        StringBuffer headerStr = new StringBuffer();
        headerStr.append("<Header>");
        headerStr.append("<MessageReferenceNumber>").
                append(messageReferenceNumber).append("</MessageReferenceNumber>");
        headerStr.append("<DocumentName>").
                append(documentName).append("</DocumentName>");
        headerStr.append("<SenderCode>").
                append(senderCode).append("</SenderCode>");
        headerStr.append("<ReceiverCode>").
                append(receiverCode).append("</ReceiverCode>");
        headerStr.append("<MessageSendingDateTime>").
                append(sendDate).append("</MessageSendingDateTime>");
        headerStr.append("</Header>");
        return headerStr.toString();
    }

    public static String getNoteStr(String n, Object v) {
        StringBuffer sb = new StringBuffer();
        if (v == null) {
            v = "";
        }
        sb.append("<" + n + ">").append(v).append("</" + n + ">");
        return sb.toString();
    }
}
