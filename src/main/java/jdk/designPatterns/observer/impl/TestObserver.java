package jdk.designPatterns.observer.impl;

import jdk.designPatterns.observer.Observer;
import org.junit.Test;

/**
 * 观察者模式(Observer)
 * 基本概念:
 * 观察者模式属于行为型模式，其意图是定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。
 * 这一个模式的关键对象是目标（Subject）和观察者（Observer）。一个目标可以有任意数目的依赖它的观察者，一旦目标的状态发生改变，
 * 所有的观察者都得到通知，作为对这个通知的响应，每个观察者都将查询目标以使其状态与目标的状态同步。
 * 适用场景：
 * 观察者模式，用于存在一对多依赖关系的对象间，当被依赖者变化时，通知依赖者全部进行更新。因此，被依赖者，应该有添加/删除依赖者的方法，
 * 且可以将添加的依赖者放到一个容器中；且有一个方法去通知依赖者进行更新。
 *
 * @author Administrator
 */
public class TestObserver {

    @Test
    public void test() {
        Lixi lixi = new Lixi();
        Observer observer = new PersonBankMoney("张三", 1000d, lixi);
        lixi.addObserver(observer);
        lixi.setLixi(2d);
        System.out.println(observer.toString());
        lixi.setLixi(3d);
        System.out.println(observer.toString());
    }
}
