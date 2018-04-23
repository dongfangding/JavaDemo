package jdk.designPatterns.strategy.test.impl;

import jdk.designPatterns.strategy.test.AttackBehavior;
import jdk.designPatterns.strategy.test.Role;

/**
 * 构建一个国王的角色
 *
 * @author ddf 2016年9月27日下午2:34:23
 */
public class KingRole extends Role {

    /**
     * 给每个角色一个默认攻击行为
     */
    public KingRole() {
        attackBehavior = new DefaultAttackBehavior();
    }

    /**
     * 有参默认一个攻击行为
     *
     * @param attackBehavior
     */
    public KingRole(AttackBehavior attackBehavior) {
        super.setAttackBehavior(attackBehavior);
    }

    @Override
    public void display() {
        System.out.println("我是一个国王！");
    }
}
