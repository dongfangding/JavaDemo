package main.java.jdk.designPatterns.adapter;

import org.junit.Test;

public class TestAdapter {
	
	@Test
	public void test() {
		Target target = new Target();
		System.out.println(target.getAmount2(3, 4, 1.2));
		System.out.println(target.getAmount2(3, 4, 0));
	}
}
