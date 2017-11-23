package main.java.jdk.other;

import java.text.ParseException;
import java.util.Date;

public class SimpleDateFormat {
	public static void main(String args[]) {
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String str = "20140211165327";
		java.text.SimpleDateFormat dateFormatSSS = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String timeStr = "2016-08-19 12:41:02:713 ";
		try {
			System.out.println(dateFormat.parse(str));
			Date date = dateFormatSSS.parse(timeStr);
			System.out.println("timeStr parse process:" + dateFormatSSS.parse(timeStr));
			System.out.println(dateFormat.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
