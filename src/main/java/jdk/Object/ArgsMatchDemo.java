package main.java.jdk.Object;

public class ArgsMatchDemo {

	public static void main(String[] args) {
		Object obj = 1;
		ArgsMatchDemo.setArgs((Integer) obj);
	}
	
	public static void setArgs(Integer i) {
		i ++;
		System.out.println(i);
	}

}
