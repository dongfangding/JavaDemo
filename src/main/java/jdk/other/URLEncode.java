package main.java.jdk.other;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLEncode {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String name = "中国";
		name = URLEncoder.encode(name, "UTF-8");
		System.out.println(name);
		name = URLDecoder.decode(name, "UTF-8");
		System.out.println(name);
	}

}
