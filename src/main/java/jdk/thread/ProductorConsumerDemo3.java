package main.java.jdk.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 模拟功能， 对资源水进行操作，有两个线程任务，注水和放水任务。创建四个线程，其中两个去操作注水，两个操作放水。
 * 		只要水的容量没有超过30，则注水任务可一直进行。而放水任务，一旦水的容量被放空，则只能等待重新注水之后，才能继续放水。
 * 		以此循环往复，保证杯中的水能够一直加水和放水，永不停歇。
 * @author DingDongfang
 *
 */
public class ProductorConsumerDemo3 {
	public static void main(String[] args) {
		// 创建线程操作的资源对象，水
		Water water = new Water();
		// 创建注水线程任务对象
		PushWaterThread pushWater = new PushWaterThread(water);
		// 创建线程
		Thread t0 = new Thread(pushWater);
		// 开启线程
		t0.start();
		
		PushWaterThread pushWater1 = new PushWaterThread(water);
		Thread t1 = new Thread(pushWater1);
		t1.start();
		
		
		PullWaterThread pullWater = new PullWaterThread(water);
		Thread t2 = new Thread(pullWater);
		t2.start();
		
		PullWaterThread pullWater1 = new PullWaterThread(water);
		Thread t3 = new Thread(pullWater1);
		t3.start();
	}
}

class PushWaterThread implements Runnable {
	private Water water;
	PushWaterThread(Water water) {
		this.water = water;
	}
	
	public void run() {
		while(true) {
			water.pushWater();
		}
	}
}

class PullWaterThread implements Runnable {
	private Water water;
	PullWaterThread(Water water) {
		this.water = water;
	}
	
	public void run() {
		while(true) {
			water.pullWater();
		}
	}
}


/**
 * 同时对水的容量进行注入和放出。当杯中的水达到一定的容量时，则停止注入。
 * 
 * @author DingDongfang
 *
 */
class Water {
	private List<Integer> waterList = new ArrayList<Integer>();
	// 锁对象
	private Lock lock = new ReentrantLock();
	// 放水的监视器对象
	private Condition pullCondition = lock.newCondition();
	// 注水的监视器对象
	private Condition pushCondition = lock.newCondition();

	public void pushWater() {
		try {
			lock.lock();
			while(waterList != null && waterList.size() == 30) {
				pushCondition.await();
			}
			// 每次注入1
			waterList.add(waterList.size() + 1);
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName()+"加水后，当前容量:" + waterList.size());
			// 唤醒线程，该线程能否获得执行权，还要依赖线程能否抢得锁从而才能获得执行权。
			pullCondition.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	
	public void pullWater() {
		try {
			lock.lock();
			while(waterList == null || waterList.size() == 0) {
				pullCondition.await();
			}
			// 每次注入1
			waterList.remove(waterList.size()-1);
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName()+"放水后，当前容量:" + waterList.size());
			pushCondition.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
