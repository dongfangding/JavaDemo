package main.java.jdk.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 此类紧接这ProductorConsumerDemo的类，进而引申出JDK对生产者消费者模式提供的解决方案。
 * Lock接口：替换了同步代码块和同步函数，将同步的隐式锁操作变成了对象操作，同时更为灵活，可以一个锁上加上不同的
 * 		　　　监视器来分别对应不同的线程任务。
 * 			new ReentrantLock() 创建一个锁对象
 * 			lock() 获取锁，加锁
 * 			unlock() 释放锁，一般定义在finally代码块中
 * 
 * Condition接口： 监视器对象，可以通过一个锁来获得多个监视器对象。
 * 			lock.newCondition() 获得一个监视器对象
 * 			await() 替换了wait方法，让线程沉睡
 * 			signal() 唤醒调用者监视器的线程任务
 * 
 * 组合操作的原理
 * 		在资源中创建Lock锁对象，通过锁对象获得多个监视器接口，分别对应不同的线程任务；在线程任务的开始，首先根据锁对象调用
 * 		lock方法，给任务上锁，并在finally中调用unlock中释放锁。在操作的具体中，如果牵扯到沉睡和唤醒操作，使用创建
 * 		的监视器对象来分别操作线程任务的代码。
 * 
 * @author Administrator
 */
public class ProductorConsumerDemo2 {
	public static void main(String args[]) {
		// 创建资源
		Resource3 r = new Resource3();
		// 传入创建的资源，用来初始化输入任务
		Input3 input = new Input3(r);
		// 将输入任务放到线程中
		Thread t0 = new Thread(input);
		// 开启输入线程
		t0.start();
		
		// 传入创建的资源，用来初始化输入任务
		Input3 input1 = new Input3(r);
		// 将输入任务放到线程中
		Thread t1 = new Thread(input1);
		// 开启输入线程
		t1.start();

		// 传入创建的资源，用来初始化输出任务
		Output3 output = new Output3(r);
		// 将输出任务放到线程中
		Thread t2 = new Thread(output);
		// 开启输出线程
		t2.start();
		
		Output3 output1 = new Output3(r);
		// 将输出任务放到线程中
		Thread t3 = new Thread(output1);
		// 开启输出线程
		t3.start();
	}
}

/**
 * 资源类 输入和输出方法，对方法进行枷锁，使用同步函数
 * 重点是，wait和notify方法，只能对当前对象锁生效，如果存在多个对象锁，则另外一个对象锁的方法无法对当前 线程产生影响。
 * 
 * @author Administrator
 *
 */
class Resource3 {
	private String userName;
	private String sex;
	private boolean flag = false;
	private int index = 0;
	// 创建一个锁对象
	private Lock lock = new ReentrantLock();
	// 创建生产者监视器对象（输入任务）
	Condition productor = lock.newCondition();
	// 创建消费者监视器对象（输出方法）
	Condition consumer = lock.newCondition();
	
	public int getIndex() {
		return index;
	}
	
	/**
	 * 使用自定义的锁对象和监视器对象，对资源输入线程任务进行加锁操作，替代了同步函数的隐式操作锁对象。
	 * @param userName
	 * @param sex
	 */
	public void set(String userName, String sex) {
		try {
			// 加锁
			lock.lock();
			while (this.flag) {
				// 调用生产者监视器的沉睡方法，释放执行权
				productor.await();;
			}
			this.userName = userName;
			this.sex = sex;
			this.index ++;
			this.userName += this.index;
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName() + "---count: "
					+ "输入：姓名:" + this.userName + "--------性别:" + sex);
			this.flag = true;
			// 调用消费者监视器的唤醒方法，来唤醒输出线程来操作任务。
			consumer.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 释放锁
			lock.unlock();
		}
	}
	
	/**
	 * 使用自定义的锁对象和监视器对象，对资源输出线程任务进行加锁操作，替代了同步函数的隐式操作锁对象。
	 */
	public void out() {
		try {
			// 加锁操作
			lock.lock();
			while (!this.flag) {
				// 调用消费者监视器对象的await方法让消费者线程沉睡，释放执行权
				consumer.await();
			}
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName() + "---count: "
					+"输出：姓名:" + userName + "--------性别:" + sex + "\n");
			this.flag = false;
			// 唤醒生产者线程
			productor.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}

class Input3 implements Runnable {
	private Resource3 r3;
	
	Input3(Resource3 r3) {
		this.r3 = r3;
	}

	@Override
	public void run() {
		while (true) {
			r3.set("丁东方", "男");
		}
	}
}

class Output3 implements Runnable {
	private Resource3 r;

	Output3(Resource3 r) {
		this.r = r;
	}

	@Override
	public void run() {
		while (true) {
			r.out();
		}
	}
}
