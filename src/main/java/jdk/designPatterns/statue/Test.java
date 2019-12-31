package jdk.designPatterns.statue;

/**
 * State状态模式定义：
 * 不同的状态,不同的行为; 或者说,每个状态有着相应的行为.
 * 适用场合：
 * State模式在实际使用中比较多,适合"状态的切换".因为我们经常会使用If elseif else 进行状态切换, 如果针对状态的这样判断切换反复出现,我们就要联想到是否可以采取State模式了.
 * 适合于内部状态,不断循环变化的.
 * 一个state,包括两部分: 对象 + 对象内部的属性(属性接口+具体属性)
 * 一个对象,要有其属性,以及其setter,getter.且设置好其初始状态+一个调用显示状态的方法(里面就是状态调用自身的显示方法).
 * 一个属性接口,应该有一个执行的方法.
 * 一个具体属性,须包含对象进去,实现方法中,须设置对象下一个要显示的属性-->从而在对象下次调用方法时,其属性值会变化.
 * <p>
 * 状态模式与观察者模式的区别：
 * 状态模式，也跟观察者模式一样，是一对多的模式。但观察者模式是“一”变了，所有的“多”也会更新。
 * 状态模式，强调的是：“多”是“一”的各个状态，“一”的各个状态，进行不断的循环。
 * <p>
 * 如何建立一与多的关系：
 * “多”，都是实现一个接口的。所以，在“一”的类中，声明的是“多”的接口；若“多”中要建立与“一”的关系，只须直接在类中声明“一”即可。
 *
 * @author DDf on 2016年8月11日下午4:12:59
 */
public class Test {
    public static void main(String[] args) {
        Light light = new Light();
        //初始调用为绿灯（构造方法中指定）
        // showColor调用Color接口中的show方法，展示当前颜色，之后调用setColor方法改变Color接口的实现类
        light.showColor();
        // 每调用一次，在Color实现类的setColor中指定下一次Color的实现类以达到改变颜色
        light.showColor();
        light.showColor();
        light.showColor();
        light.showColor();
        light.showColor();
    }
}
