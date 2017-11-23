package main.java.jdk.designPatterns.strategy.test.impl;

import main.java.jdk.designPatterns.strategy.test.AttackBehavior;

/**
 * 默认的攻击行为
 * @author ddf 2016年9月27日下午2:32:56
 *
 */
public class DefaultAttackBehavior implements AttackBehavior {

	@Override
	public void attack() {
		System.out.println("我现在的攻击行为是：默认行为，赤手空拳！");
	}
}
