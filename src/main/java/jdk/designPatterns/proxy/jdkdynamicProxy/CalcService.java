package jdk.designPatterns.proxy.jdkdynamicProxy;

/**
 * 计算接口$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 22:48
 */
public interface CalcService {

    /**
     * 计算结果
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    int calc(int a , int b, int c, int d);
}
