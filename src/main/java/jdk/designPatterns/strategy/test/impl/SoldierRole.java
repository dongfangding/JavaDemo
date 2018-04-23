package jdk.designPatterns.strategy.test.impl;


import jdk.designPatterns.strategy.test.AttackBehavior;
import jdk.designPatterns.strategy.test.Role;

public class SoldierRole extends Role {

    public SoldierRole() {
        super.attackBehavior = new DefaultAttackBehavior();
    }

    public SoldierRole(AttackBehavior attackBehavior) {
        super.setAttackBehavior(attackBehavior);
    }

    @Override
    public void display() {
        System.out.println("我是一个士兵！");
    }

}
