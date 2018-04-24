package jdk.designPatterns.strategy.test.impl;


import jdk.designPatterns.strategy.test.Role;

public class TestMain {
    public static void main(String[] args) {
        Role kingRole = new KingRole();
        kingRole.display();
        kingRole.performAattack();
        kingRole.setAttackBehavior(new SpearAttackBehavior());
        Role soldier = new SoldierRole();
        soldier.display();
        soldier.performAattack();
        soldier.setAttackBehavior(new IronSwordAttackBehavior());
    }
}
