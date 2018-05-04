package jdk.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

;

/**
 * 制定带参数的运行器
 */
@RunWith(Parameterized.class)
public class CalculatorParamTest {
    private Calculator calculator;
    private int num;
    private int result;

    public CalculatorParamTest(int num, int result) {
        this.num = num;
        this.result = result;
        calculator = new Calculator();
    }

    /**
     * 使用参数初始化构造函数
     *
     * @return
     */
    public static Collection<Integer[]> data() {
        // 第一个参数是测试参数，第二个是预期结果
        return Arrays.asList(new Integer[][]{
                {-2, 0},
                {0, 2},
                {2, 4}
        });
    }

    /**
     * 分别测试对正数和负数和0的加法计算
     */
    @Test
    public void testAdd() {
        // 初始化加2
        calculator.add(2);
        // 再加上参数
        calculator.add(num);
        assertEquals(result, calculator.getResult());
    }

}
