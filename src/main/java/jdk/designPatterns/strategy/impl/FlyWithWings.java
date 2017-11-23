package main.java.jdk.designPatterns.strategy.impl;

import main.java.jdk.designPatterns.strategy.FlyBehavior;

/**
 * 具体描述的飞的行为的类	
 * @author Administrator
 */
public class FlyWithWings implements FlyBehavior{
	@Override
	public void fly() {
		System.out.println("随风而起");
	}
}
