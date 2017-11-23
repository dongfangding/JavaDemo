package main.java.jdk.regex;

public class StartEnd {
	public static void main(String args[]) {
		String str = "abcd";
		String regex = "^abc";
		System.out.println(str.matches(regex));
	}
}
