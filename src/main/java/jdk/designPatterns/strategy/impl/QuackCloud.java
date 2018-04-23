package jdk.designPatterns.strategy.impl;

import jdk.designPatterns.strategy.QuackBehavior;

/**
 * 具体描述叫的行为的实现类
 *
 * @author Administrator
 */
public class QuackCloud implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("扯着嗓子吼叫");
    }

}
