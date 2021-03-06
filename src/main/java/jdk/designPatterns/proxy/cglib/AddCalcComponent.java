package jdk.designPatterns.proxy.cglib;

/**
 * 加法计算类$
 *
 * @author dongfang.ding
 * @date 2020/10/28 0028 23:11
 */
public class AddCalcComponent {

    private int id;

    AddCalcComponent() {

    }

    public AddCalcComponent(int id) {
        this.id = id;
    }

    public int add(int a, int b, int c, int d) {
        return a + b + c + d + id;
    }
}
