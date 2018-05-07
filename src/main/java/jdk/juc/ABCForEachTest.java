package jdk.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DDf on 2018/5/4
 * 编写一个程序，开启 3 个线程，这三个线程的 ID 分别为
 * A、B、C，每个线程将自己的 ID 在屏幕上打印 10 遍，要
 * 求输出的结果必须按顺序显示。
 * 如：ABCABCABC…… 依次递归
 */
public class ABCForEachTest {
    public static void main(String[] args) {
        Task task = new Task();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                task.printA();
            }
        }, "线程A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                task.printB();
            }
        }, "线程B").start();


        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                task.printC();
            }
        }, "线程C").start();

    }
}

class Task {
    private Lock lock = new ReentrantLock();
    private Integer currTask = 1;
    private Condition conditionA = lock.newCondition();
    private Condition conditionB = lock.newCondition();
    private Condition conditionC = lock.newCondition();


    public void printA() {
        lock.lock();
        try {
            if (currTask != 1) {
                try {
                    conditionA.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // System.out.println(Thread.currentThread().getName() + "| A");
            System.out.print("A");
            currTask = 2;
            conditionB.signal();
            /**
             * 如果不加这一行代码，有可能会出现A虽然唤醒B，但是A下次依然有争取执行权的能力，会再次获取，然后循环在
             * 这一块执行，有时间的不确定性，但是加了这一块最后会出现三个线程在沉睡，而导致进程不会结束
             */
            // conditionA.await();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        } */ finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            if (currTask != 2) {
                try {
                    conditionB.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // System.out.println(Thread.currentThread().getName() + "| B");
            System.out.print("B");
            currTask = 3;
            conditionC.signal();
            //conditionB.await();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        } */ finally {
            lock.unlock();
        }
    }


    public void printC() {
        lock.lock();
        try {
            if (currTask != 3) {
                try {
                    conditionC.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // System.out.println(Thread.currentThread().getName() + "| C");
            System.out.print("C");
            currTask = 1;
            conditionA.signal();
            // conditionC.await();
        }/* catch (InterruptedException e) {
            e.printStackTrace();
        } */ finally {
            lock.unlock();
        }
    }
}
