package jdk.interview;

public class StringEquals {

	public static void main(String[] args) {

		String str = "hello";
		String str1 = "hello";
		String str2 = "he" + new String("llo");
		String str3 = new String("hello");
		System.out.println(str == str1);
		System.out.println(str == str2);
		System.out.println(str == str3);
		System.out.println(str == str3.intern());
	}
}
