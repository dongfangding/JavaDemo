package main.java.jdk.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NowBeforeDays {

	public static void main(String[] args) {
		Date nowDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowDate);
		calendar.add(Calendar.DAY_OF_MONTH, -33);
		
		Date beforeDate = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("nowDate: " + format.format(nowDate));
		System.out.println("beforeDate: " + format.format(beforeDate));
	}

}
