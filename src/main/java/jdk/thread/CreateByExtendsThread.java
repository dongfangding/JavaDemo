package main.java.jdk.thread;

public class CreateByExtendsThread {
	public static void main(String args[]) {
		Scope s1 = new Scope("线程1");
		Scope s2 = new Scope("线程2");
		// 开启线程，有JVM调用线程的run方法
		s1.start();
		s2.start();
		// 获得当前运行线程
		System.out.println("主线程over!" + Thread.currentThread().getName());
		
		
		/**
		 * 测试异常
		 * 多线程中，每个线程的异常不影响别的线程继续运行
		 */
		/*int []arr = new int[3];
		System.out.println(arr[3]);*/
	}
}

/**
 * 
 * @author ddf 2016年10月11日下午5:19:51
 * 创建线程的方式
 * 1. 继承Thread类，重写run方法。
 * 2. 将线程需要执行的任务代码运行在run方法体中
 * 3. 创建线程实例， 调用start方法，开启线程，并由jvm来负责自动调用run方法
 *
 */
class Scope extends Thread{
	private String name;
	Scope(String name) {
		// 给线程取名（默认为Thread_编号（从0开始））
		super(name);
		this.name = name;
	}
	
	
	public void total() {
		for(int i = 0; i < 20; i ++) {
			System.out.println("name:" + name + "，输出：" + i 
					+ "," + Thread.currentThread().getName());
		}
	}
	
	public void exception() {
		System.out.println(4/0);
	}

	@Override
	public void run() {
		total();
		// 测试异常
		// exception();
	}
}




