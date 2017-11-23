package main.java.jdk.designPatterns.strategy.impl;

import main.java.jdk.designPatterns.strategy.Duck;

/**
 * 鸭子的实现类
 * @author Administrator
 */
public class WhiteDuck extends Duck{
	public WhiteDuck() {
		super.setFlyBehavior(new FlyNoWay());
		super.setQuackBehavior(new QuackNoWay());
	}
	
	@Override
	public void display() {
		super.display();
		System.out.println("这是一只白色的鸭子");
	}
}
