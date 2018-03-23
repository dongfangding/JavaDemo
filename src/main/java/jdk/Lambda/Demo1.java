/**
 * 
 */
package main.java.jdk.Lambda;

import java.util.function.Consumer;

import org.junit.Test;

/**
 * @author DDF 2018年3月22日
 *
 */
public class Demo1 {

	
	@Test
	public void test1() {
		Consumer<Integer> con = x -> System.out.println(x);
		con.accept(formula(1, 2, (num1, num2) -> num1 + num2));
	}
	
	public static int formula(int num1, int num2, Formula formula) {
		return formula.formula(num1, num2);
	}
}

