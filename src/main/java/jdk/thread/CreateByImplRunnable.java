package main.java.jdk.thread;

/**
 * 创建线程的第二种方法， 实现Runnable接口
 * 1. 创建自己的线程任务类代码，并实现Runnable接口
 * 2. 复写run方法，运行自己的任务方法
 * 3. 调用时创建Thread类对象，并将实现Runnable接口的任务类实例对象传入Thread的构造函数入参
 * 4. 调用线程实例化对象的start方法，开启线程
 * @author DingDongfang
 *
 */
public class CreateByImplRunnable {
	public static void main(String args[]) {
		MyTask mt = new MyTask();
		Thread t1 = new Thread(mt);
		t1.start();
		
		Thread t2 = new Thread(t1);
		t2.start();
	}
}

/**
 * 线程任务
 * @author DingDongfang
 *
 */
class MyTask implements Runnable {
	public void total() {
		for(int i = 0; i < 20; i ++) {
			System.out.println("输出：" + i 
					+ "," + Thread.currentThread().getName());
		}
	}
	@Override
	public void run() {
		total();
	}
	
}
