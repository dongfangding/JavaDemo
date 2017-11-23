package main.java.jdk.other;

import java.util.Date;

public class Data {

	public static void main(String[] args) {
		System.out.println(new Date().getTime());
		long time = 1439877702073L;
		long time1 = new Date().getTime();
		long a = (time1 - time) / 1000 / 60;
		System.out.println("等待");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(a);
	}
}
