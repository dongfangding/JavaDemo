package main.java.jdk.designPatterns.strategy.impl;

import main.java.jdk.designPatterns.strategy.QuackBehavior;

/**
 * 具体描述叫的实现类
 * @author Administrator
 */
public class QuackNoWay implements QuackBehavior {
	@Override
	public void quack() {
		System.out.println("不会叫");
	}
}
