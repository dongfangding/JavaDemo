package jdk.designPatterns.strategy.test;

/**
 * 定义一个角色的超类
 *
 * @author ddf 2016年9月27日下午2:20:11
 */
public abstract class Role {
    public AttackBehavior attackBehavior;

    public void performAattack() {
        attackBehavior.attack();
    }

    public abstract void display();


    /**
     * @param ab the ab to set
     *           用于动态改变攻击行为
     */
    public void setAttackBehavior(AttackBehavior ab) {
        System.out.println("现在我要切换攻击方式，装载：" + ab.getClass().getSimpleName());
        this.attackBehavior = ab;
        // 对于切换武器来说，自动改变攻击方式
        performAattack();
    }


}
