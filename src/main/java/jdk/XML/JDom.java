package main.java.jdk.XML;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class JDom {
	public static void main(String []args){
		//parseXml1();
		parseXml2();
	}
	
	public static void parseXml1(){
		String content = "<STUDENTS><TIME>20150703</TIME><DATA><STUDENT><NAME>DDF</NAME><NO>001</NO></STUDENT>"
				+ "<STUDENT><NAME>YICHEN</NAME><NO>002</NO></STUDENT></DATA></STUDENTS>";
		try{
			// 将字符串解析成xml
			org.jdom2.Document doc = new SAXBuilder().build(new StringReader(content));
			// 获取根节点
			Element element = doc.getRootElement();
			// 获取没有子节点Time节点元素用getTextTrim()
			String timeStr = element.getChildTextTrim("TIME",element.getNamespace());
			System.out.println("TIME节点元素内容是" + timeStr);
			
			// 也可以直接获取到节点调用getText()获取
			Element timeEle = element.getChild("TIME", element.getNamespace());
			String timeStr1 = timeEle.getTextTrim();
			System.out.println(timeStr1);
			
			// 获取DATA节点
			Element data = element.getChild("DATA", element.getNamespace());
			// 获取DATA节点所有的子节点
			List<Element> eleList = data.getChildren("STUDENT");
			Iterator<Element> iter = eleList.iterator();
			System.out.println("STUDENT节点内容");
			while(iter.hasNext()){
				// 获取每一个STUDENT节点
				Element ele = (Element) iter.next();
				// 获取子节点元素内容用getChildTextTrim
				String name = ele.getChildTextTrim("NAME");
				String no = ele.getChildTextTrim("NO");
				System.out.println("name:" + name + "\tno:" + no);
			}
			System.out.println("可以更改原xml结构内容");
			// 创建一个新的STUDENT节点
			Element student = new Element("STUDENT");
			// 创建一个新的NAME节点，并设置文本
			Element name = new Element("NAME");
			name.setText("haha");
			// 设置一个新的NO节点，并设置文本
			Element no = new Element("NO");
			no.setText("003");
			// 将NAME节点和NO节点放在STUDENT节点中
			student.addContent(name);
			student.addContent(no);
			// 将STUDENT节点放在DATA节点中
			data.addContent(student);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void parseXml2(){
		String content = "<STUDENTS><TIME>20150703</TIME><DATA><STUDENT><NAME>DDF</NAME><NO>001</NO></STUDENT>"
				+ "<STUDENT><NAME>YICHEN</NAME><NO>002</NO></STUDENT></DATA></STUDENTS>";
		SAXBuilder sax = new SAXBuilder();
		try {
			Document doc = sax.build(new StringReader(content));
			Element root = doc.getRootElement();
			List<Element> students = root.getChild("DATA").getChildren("STUDENT");
			System.out.println("---------------------把xml放到map中循环------------------");
			if(students != null && students.size() > 0) {
				for(Element ele : students) {
					Map<String, String> resultMap = new HashMap<String, String>();
					parseXmlToMap(ele, resultMap);
					if(resultMap != null && resultMap.size() > 0) {
						for(Map.Entry<String, String> mp : resultMap.entrySet()) {
							System.out.println(mp.getKey() + ":" + mp.getValue());
						}
					}
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void parseXmlToMap(Element element, Map<String, String> resultMap) {
		if(element != null) {
			List<Element> eleList = element.getChildren();
			if(eleList != null && eleList.size() > 0) {
				for(Element el : eleList) {
					resultMap.put(el.getName(), el.getValue());
				}
			}
		}
	}
}
