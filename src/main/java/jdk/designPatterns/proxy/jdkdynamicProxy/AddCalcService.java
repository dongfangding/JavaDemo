package jdk.designPatterns.proxy.jdkdynamicProxy;

/**
 * $
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 22:48
 */
public class AddCalcService implements CalcService {

    private String id;

    public AddCalcService() {}

    public AddCalcService(String id) {
        this.id = id;
    }

    /**
     * 计算结果
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    @Override
    public int calc(int a, int b, int c, int d) {
        return a + b + c + d;
    }
}
