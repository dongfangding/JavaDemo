package main.java.webservice.JAXB;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import main.java.webservice.entity.Student;

public class JAXB {
	
	@Test
	public void marShal() {
		try {
			Student stu = new Student(1, "ddf", "123456");
			JAXBContext jct = JAXBContext.newInstance(Student.class);
			Marshaller ms = jct.createMarshaller();
			ms.marshal(stu, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void unMarshal() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<student><id>1</id><userName>ddf</userName><password>123456"
				+ "</password></student>";
		try {
			JAXBContext jct = JAXBContext.newInstance(Student.class);
			Unmarshaller unMs = jct.createUnmarshaller();
			Student student = (Student) unMs.unmarshal(new StringReader(xml));
			System.out.println(student.toString());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
