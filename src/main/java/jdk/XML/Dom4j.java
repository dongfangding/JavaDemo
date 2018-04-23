package jdk.XML;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Dom4j {
    public static void main(String args[]) {
        String xml = "<STUDENTS><TIME>20150703</TIME><STUDENT><NO flag='one'>001</NO><NAME>DDF</NAME>"
                + "</STUDENT><STUDENT><NAME>YICHEN</NAME><NO flag = 'two'>002</NO></STUDENT></STUDENTS>";
        Element root = getRootByDom4j(xml);
        Map<String, String> resultMap = new HashMap<String, String>();
        System.out.println("--------------getAllChildren----------------");
        getAllChildren(root, resultMap);

        // 创建文档
        createDocument();

        // 解析格式化后的文件
        // 获得指定子节点
        parseDetail(root);

        selectNodeByXPath(root);

        System.out.println("-----------getAllChildren---------------");
        if (resultMap.size() > 0) {
            for (Map.Entry<String, String> re : resultMap.entrySet()) {
                System.out.println(re.getKey() + ":" + re.getValue());
            }
        }
    }

    public static void parseDetail(Element root) {
        System.out.println("---------parseDetail-------------");
        Element element = root.element("TIME");
        System.out.println("获得单个子节点");
        System.out.println(element.getName() + ":" + element.getStringValue());
        // 获得子节点的List（不能获得子节点）
        List<Element> elementList = root.elements("STUDENT");
        System.out.println("获得指定子节点的集合");
        for (Element el : elementList) {
            System.out.println(el.getName() + ":" + el.getStringValue());
        }
        // Iterator
        Iterator<Element> eleIter = root.elementIterator("STUDENT");
        System.out.println("----------------Iterator------------");
        while (eleIter.hasNext()) {
            Element ele = eleIter.next();
            System.out.println(ele.getName());
            for (int i = 0; i < ele.nodeCount(); i++) {
                Element clEle = (Element) ele.node(i);
                System.out.println(clEle.getName() + ":" + clEle.getStringValue());
            }
            System.out.println("- - - - -- -- ");
        }
    }

    public static Element getRootByDom4j(String xml) {
        SAXReader saxReader = new SAXReader();
        org.dom4j.Element root = null;
        if (xml.trim() != null && xml.trim() != "") {
            try {
                Document document = DocumentHelper.parseText(xml);
                root = document.getRootElement();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    public static void selectNodeByXPath(Element root) {
        System.out.println("-----------selectNodeByXPath---------------");
        System.out.println("TIME:" + root.selectSingleNode("/STUDENTS/TIME").getStringValue());
        System.out.println("所有的STUDENT" + root.selectNodes("//STUDENT"));
        List<Element> eleList = root.selectNodes("/STUDENTS/STUDENT/NO[@flag]");
        for (Element e : eleList) {
            System.out.println("所有的属性为flag的NO：" + e.getStringValue());
        }
        System.out.println("属性flag='one'的NO节点： " + root.selectSingleNode("/STUDENTS/STUDENT/NO[@flag='one']").getStringValue());
    }

    /**
     * 将当前节点的直接子节点的数据放到map中，循环使用时不能装进同一个map,只能取直接子节点
     *
     * @param element
     * @param resultMap
     */
    public static void getSingleMap(Element element, Map<String, String> resultMap) {
        if (element != null && element.nodeCount() > 0) {
            for (int i = 0; i < element.nodeCount(); i++) {
                Element clElement = (Element) element.node(i);
                resultMap.put(clElement.getName(), clElement.getStringValue());
            }
        }
    }

    /**
     * 将当前节点的所有子节点的数据放到map中，循环使用时不能装进同一个map
     *
     * @param element
     * @param resultMap
     */
    public static void getAllChildren(Element element, Map<String, String> resultMap) {
        System.out.println(element.getName());
        if (element != null && element.nodeCount() > 0) {
            for (int i = 0; i < element.nodeCount(); i++) {
                if (element.node(i) instanceof Element) {
                    Element clElement = (Element) element.node(i);
                    if (!clElement.isTextOnly()) {
                        resultMap.put(clElement.getName(), clElement.getStringValue());
                        System.out.println(clElement.getName() + ":" + clElement.getStringValue());
                        if (clElement.nodeCount() > 0) {
                            getAllChildren(clElement, resultMap);
                        }
                    }
                }
            }
        }
    }

    public static void createDocument() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Root");
        Element header = root.addElement("Header");
        Element messageRefEle = header.addElement("MessageReferenceNumber");
        messageRefEle.setText("depot取消");
        Element documentNameEle = header.addElement("DocumentName");
        documentNameEle.setText("E_EXP_CANCEL");
        Element senderCodeEle = header.addElement("SenderCode");
        senderCodeEle.setText("AMAZON");
        Element ReceiverCode = header.addElement("ReceiverCode");
        ReceiverCode.setText("EASTTOP");
        Element MessageSendingDateTime = header.addElement("MessageSendingDateTime");
        MessageSendingDateTime.setText(new Date().toString());

        Element Shipment = root.addElement("Shipment");
        Element shipmentID = Shipment.addElement("shipmentID");
        shipmentID.setText("EX1605230002");
        Element remarks = Shipment.addElement("remarks");
        remarks.setText("remarks");

        // 创建字符串缓冲区
        FileWriter fw = null;
        try {
            File file = new File("C:" + File.separator + "IN" + File.separator);
            if (!file.exists()) {
                file.mkdirs();
            }
            fw = new FileWriter(file + File.separator + "teststst.xml");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // 设置文件编码
        OutputFormat xmlFormat = new OutputFormat();
        xmlFormat.setEncoding("UTF-8");
        // 设置换行
        xmlFormat.setNewlines(true);
        // 生成缩进
        xmlFormat.setIndent(true);
        // 使用4个空格进行缩进, 可以兼容文本编辑器
        xmlFormat.setIndent("    ");

        // 创建写文件方法
        XMLWriter xmlWriter = new XMLWriter(fw, xmlFormat);
        // 写入文件
        try {
            xmlWriter.write(document);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 关闭
        try {
            xmlWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 输出xml
        System.out.println("-----------------------------------创建xml ----------------------------------");
        System.out.println(document.asXML());

    }
}
