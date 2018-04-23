package jdk.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 打包测试
 */
@RunWith(Suite.class)
@SuiteClasses({CalculatorParamTest.class, CalculatorTest.class})
public class AllSuiteTests {
    // 类里不需要代码
}
