package main.java.jdk.IO;

import java.io.File;

public class GetFileLastName {
	public static void main(String args[]) {
		File file = new File("C:" + File.separator + "TEST" + File.separator + "name" + File.separator);
		if(!file.exists()) {
			file.mkdirs();
		}
		System.out.println(file.getName());
	}
}
