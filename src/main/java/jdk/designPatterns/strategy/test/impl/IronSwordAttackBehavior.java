package main.java.jdk.designPatterns.strategy.test.impl;

import main.java.jdk.designPatterns.strategy.test.AttackBehavior;

/**
 * 构造一个使用铁剑的攻击方式
 * @author ddf 2016年9月27日下午2:35:53
 *
 */
public class IronSwordAttackBehavior implements AttackBehavior {

	@Override
	public void attack() {
		System.out.println("我现在的攻击行为是：铁剑攻击！");
	}

}
