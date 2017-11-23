package main.java.jdk.designPatterns.strategy.impl;

import main.java.jdk.designPatterns.strategy.FlyBehavior;

/**
 * 具体描述飞的行为的类
 * @author Administrator
 *
 */
public class FlyNoWay implements FlyBehavior{
	@Override
	public void fly() {
		System.out.println("想飞却飞不起来");
	}

}
