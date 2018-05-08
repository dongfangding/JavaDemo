package jdk.designPatterns.iterator.impl;

import jdk.designPatterns.iterator.Iterator;
import jdk.designPatterns.iterator.IteratorController;
import jdk.designPatterns.iterator.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * (1)
 * 基本概念:
 * 迭代器模式属于行为型模式，其意图是提供一种方法顺序访问一个聚合对象中得各个元素，而又不需要暴露该对象的
 * 内部表示。
 * 至少可以历遍first,next,previous,last,isOver,或是历遍选择符合某种条件的子元素.
 * (2)
 * 结构:
 * 由一个接口与一个实现类组成.
 * 接口:
 * 主要是定义各历遍的方法.
 * 实现类:
 * 需要一个计算点private int current=0 ; 以及一个容器Vector,来存在原来的进行历遍的一团东西;再对接口方法进
 * 行实现
 *
 * @author Administrator
 */
public class TestIterator {

    @Test
    public void test() {
        Student student = new Student();
        student.setName("ddf1");
        student.setAge("18");
        student.setSex("男");
        student.setStuNo("111111");
        student.setTelNo("22222");
        Student student1 = new Student();
        student1.setName("ddf2");
        student1.setAge("20");
        student1.setSex("男");
        student1.setStuNo("22222");
        student1.setTelNo("111111222222");
        Student student2 = new Student();
        student2.setName("ddf3");
        student2.setAge("23");
        student2.setSex("男");
        student2.setStuNo("33333");
        student2.setTelNo("333333333");
        List<Student> stuList = new ArrayList<Student>();
        stuList.add(student);
        stuList.add(student1);
        stuList.add(student2);
        IteratorController<Student> ic = new IteratorControllerImpl<Student>(stuList);
        Iterator<Student> iterator = ic.createIterator();
        while (!iterator.isOver()) {
            System.out.println(iterator.next().getName());
        }
    }
}
