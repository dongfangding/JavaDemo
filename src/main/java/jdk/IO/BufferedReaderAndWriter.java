package main.java.jdk.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class BufferedReaderAndWriter {
	private static final String CHARSET_UTF_8 = "UTF-8";
	private static final String LINE_SEPRATOR = System.getProperty("line.separator");
	
	public static void main(String []args) {
		String sourcePath = "D:\\BaiduYunDownload\\config\\web2.xml";
		String targetPath = "D:\\BaiduYunDownload\\config\\beans2.xml";
		readAndWrite2(sourcePath, targetPath, CHARSET_UTF_8);
	}
	
	public static void readAndWrite(String sourcePath, String targetPath, String charset) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			StringBuilder sbl = new StringBuilder();
			// 读取模版
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
						new File(sourcePath)), charset));
			// 重新写入文件的目的地
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					new File(targetPath)), charset));
			while((br.readLine() != null)) {
				sbl.append(br.readLine() + LINE_SEPRATOR);
				System.out.println(br.readLine());
			}
			bw.flush();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void readAndWrite2(String sourcePath, String targetPath, String charset) {
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(new File(sourcePath));
			fos = new FileOutputStream(new File(targetPath));
			byte[] b =new byte[1024];
			while(fis.read(b) != -1){
				fos.write(b);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
