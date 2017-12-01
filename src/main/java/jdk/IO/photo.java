package main.java.jdk.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class photo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("c://file/demo.png");
		File file1 = new File("c://file/demo1.png");
		if (!file1.exists()) {
			try {
				file1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(file1);
			byte[] b = new byte[4000];

			try {
				while (fis.read(b) != -1) {

					fos.write(b);
				}
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
