package jdk.designPatterns.strategy.test.impl;

import jdk.designPatterns.strategy.test.AttackBehavior;

/**
 * 构造一个长矛攻击方式
 *
 * @author ddf 2016年9月27日下午2:39:06
 */
public class SpearAttackBehavior implements AttackBehavior {

    @Override
    public void attack() {
        System.out.println("我现在的攻击行为是： 长矛攻击！");
    }

}
