package jdk.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DDf on 2019/4/14
 */


class PrintTask {

	private Lock lock = new ReentrantLock();
	private Condition condition1 = lock.newCondition();
	private Condition condition2 = lock.newCondition();
	private Condition condition3 = lock.newCondition();
	private volatile int taskIndex = 1;

	public void printA() {
		try {
			lock.lock();
			while (taskIndex != 1) {
				condition1.await();
			}
			for (int i = 0; i < 5; i++) {
				System.out.println(Thread.currentThread().getName() + "打印A");
			}
			taskIndex = 2;
			condition2.signal();
			/**
			 * 如果不加这一行代码，有可能会出现A虽然唤醒B，但是A下次依然有争取执行权的能力，会再次获取，然后循环在
			 * 这一块执行，有时间的不确定性，但是加了这一块最后会出现三个线程在沉睡，而导致进程不会结束
			 */
			condition1.await();
		} catch (Exception e) {} finally {
			lock.unlock();
		}
	}

	public void printB() {
		try {
			lock.lock();
			while (taskIndex != 2) {
				condition2.await();
			}
			for (int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getName() + "打印B");
			}
			taskIndex = 3;
			condition3.signal();
			condition2.await();
		} catch (Exception e) {} finally {
			lock.unlock();
		}
	}


	public void printC() {
		try {
			lock.lock();
			while (taskIndex != 3) {
				condition3.await();
			}
			for (int i = 0; i < 15; i++) {
				System.out.println(Thread.currentThread().getName() + "打印C");
			}
			taskIndex = 1;
			condition1.signal();
			condition3.await();
		} catch (Exception e) {} finally {
			lock.unlock();
		}
	}
}


public class ConditionLoopPrint {

	public static void main(String[] args) {
		PrintTask printTask = new PrintTask();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				printTask.printC();
			}
		}).start();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				printTask.printB();
			}
		}).start();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				printTask.printA();
			}
		}).start();
	}
}
