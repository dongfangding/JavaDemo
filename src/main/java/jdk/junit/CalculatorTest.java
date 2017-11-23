package main.java.jdk.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

// 默认的或者使用BlockJUnit4ClassRunner.class
@RunWith(JUnit4.class)
public class CalculatorTest {
	private Calculator calculator = new Calculator();
	
	// 测试前清除原结果，避免各自方法对数据造成影响
	@Before
	public void setUp() throws Exception {
		calculator.clear();
	}
	
	// 正常测试通过
	@Test
	public void testAdd() {
		calculator.add(2);
		calculator.add(3);
		assertEquals(5, calculator.getResult());
	}
	// 忽略某些未完成的方法加入测试
	@Ignore
	public void testIgore() {
		assertEquals(1, calculator.getResult());
	}
	
	// 设置超时时间
	@Test(timeout = 5000)
	public void testSiXunHuan() {
		calculator.siXunHian();
		assertEquals(1, calculator.getResult());
	}
	
	// 匹配是否抛出指定类型的异常
	@Test(expected = NullPointerException.class)
	public void testNull() {
		calculator.nullPointException();
	}
	
	// 结果不匹配
	@Test
	public void testSubstract() {
		calculator.add(5);
		calculator.substract(3);
		assertEquals(1, calculator.getResult());
	}

	@Test
	public void testSquare() {
		calculator.add(2);
		calculator.square(3);
		assertEquals(6, calculator.getResult());
	}

	@Test
	public void testDevide() {
		calculator.add(10);
		calculator.devide(5);
		assertEquals(2, calculator.getResult());
	}

	@Test
	public void testClear() {
		assertEquals(0, calculator.getResult());
	}
}
