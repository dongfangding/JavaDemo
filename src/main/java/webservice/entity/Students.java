package main.java.webservice.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Students {
	private List<Student> student;

	/**
	 * @return the student
	 */
	public List<Student> getStudent() {
		return student;
	}

	/**
	 * @param student the student to set
	 */
	public void setStudent(List<Student> student) {
		this.student = student;
	}

	
	
}
