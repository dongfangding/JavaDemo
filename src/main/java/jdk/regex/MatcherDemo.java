package main.java.jdk.regex;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatcherDemo {

	public static void main(String[] args) {
		Pattern p = Pattern.compile("\\?");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("test.txt")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String s = "";
		String temp = "";
		try {
			while((temp = br.readLine()) != null){
				s = s + temp;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Matcher m = p.matcher(s);
		String str[] = {"1","2"};
		StringBuffer buf = new StringBuffer();
		int i = 0;
		while(m.find()) {
			i ++;
			m.appendReplacement(buf,str[i -1 ] );
		}
		m.appendTail(buf);
		System.out.println( i + "****"+buf.toString());

	}

}
