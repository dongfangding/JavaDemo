package main.java.jdk.junit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

// 使用字段顺序来决定执行顺序
// 默认 MethodSorters.DEFAULT
// 由JVM决定 MethodSorters.JVM
// 由词典顺序决定 MethodSorters.NAME_ASCENDING
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FixMethodTest {
	
	@Test
	public void testA() {
		System.out.println("method testA is executed ! sort : 1!");
	}
	
	@Test
	public void testC() {
		System.out.println("method testC is executed ! sort : 2!");
	}
	
	@Test
	public void testB() {
		System.out.println("method testB is executed ! sort : 3!");
	}
}
