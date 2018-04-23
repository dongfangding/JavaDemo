package jdk.designPatterns.strategy.impl;

import jdk.designPatterns.strategy.Duck;

/**
 * 鸭子的实现类
 *
 * @author Administrator
 */
public class BlankDuck extends Duck {
    // 实例化Duck类中的接口实现类
    public BlankDuck() {
        super.setFlyBehavior(new FlyWithWings());
        super.setQuackBehavior(new QuackCloud());
    }

    // 重写自己的外观描述
    @Override
    public void display() {
        super.display();
        System.out.println("当前是一只黑鸦");
    }

}
