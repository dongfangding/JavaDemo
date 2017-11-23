package main.java.jdk.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 如何使线程任务停止
 * 1. stop方法
 * 2. run方法结束
 * 3. 通过定义标记的方法。即控制线程中的循环结构，通过标记的改变来终止循环
 * 
 * 实现功能，有两个线程对资源进行操作，谁先抢到50谁就获得胜利！同时线程任务终止
 * @author DingDongfang
 *
 */
public class StopThreadDemo {

	public static void main(String[] args) {
		TotalScope ts = new TotalScope();
		ScopeThread1  st1 = new ScopeThread1(ts);
		Thread t0 = new Thread(st1, "线程0");
		t0.start();
		
		
		ScopeThread2 st2 = new ScopeThread2(ts);
		Thread t1 = new Thread(st2, "线程1");
		t1.start();
	}
}


class TotalScope {
	private int scope = 0;
	private volatile boolean flag = true;
	private Lock lock = new ReentrantLock();
	private Condition c1 = lock.newCondition();
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public boolean getFlag() {
		return this.flag;
	}  
	
	/**
	 * 没有达到预期效果，沉睡的线程无法读取标识，无法退出循环。线程处于沉睡状态
	 */
	public void getScope() {
		try {
			lock.lock();
			while(!this.flag) {
				System.out.println(Thread.currentThread().getName()+"睡了"+this.scope);
				c1.await();
				return;
			}
			this.scope++;
			Thread.sleep(20);
			System.out.println(Thread.currentThread().getName() + "获得分数: " + this.scope);
			while(this.scope >= 50) {
				System.out.println(Thread.currentThread().getName() + "率先拿到分数50，获得胜利，任务终止！");
				this.flag = false;
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
			this.flag = false;
		} finally {
			lock.unlock();
		}
	}
	
	public void stop() {
		System.out.println("----------------------");
	}
}

class ScopeThread1 implements Runnable{
	private TotalScope scope;
	public ScopeThread1(TotalScope scope) {
		this.scope = scope;
	}
	
	public void run() {
		while(true) {
			if(!scope.getFlag()) {
				break;
			} else {
				scope.getScope();
				// scope.stop();
			}
		}
	}
}

class ScopeThread2 implements Runnable{
	private TotalScope scope;
	public ScopeThread2(TotalScope scope) {
		this.scope = scope;
	}
	
	public void run() {
		while(true) {
			if(!scope.getFlag()) {
				break;
			} else {
				scope.getScope();
				// scope.stop();
			}
		}
	}
}