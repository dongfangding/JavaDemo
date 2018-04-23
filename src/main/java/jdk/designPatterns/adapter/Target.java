package jdk.designPatterns.adapter;

/**
 * 适配器模式
 * 将要进行复用的类，放到目标类的构造方法中，进行实例化，然后在目标类的相应方法中，进行调用，修改原来方法
 * 中的参数，或添加相应的逻辑。即复用了已有类的原来方法。
 *
 * @author Administrator
 */
public class Target {
    private Adapter adaptee;

    public Target() {
        adaptee = new Adapter();
    }

    public double getAmount2(int num, double price, double tax) {
        if (tax == 0d) {
            return adaptee.getAmount(num, price);
        } else {
            return num * price * tax;
        }
    }
}
