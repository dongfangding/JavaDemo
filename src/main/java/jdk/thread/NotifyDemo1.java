package main.java.jdk.thread;

/**
 * 线程唤醒机制
 * 实现需求， 有一个输入线程和一个输出线程，每当输入线程对资源进行赋值之后，输出线程就要对这个资源进行输出。
 * @author Administrator
 *
 */
public class NotifyDemo1 {
	public static void main(String[] args) {
		// 创建资源
		Resource1 r1 = new Resource1();
		// 传入创建的资源，用来初始化输入任务
		Input1 input1 = new Input1(r1);
		// 将输入任务放到线程中
		Thread t0 = new Thread(input1);
		// 开启输入线程
		t0.start();
		
		
		// 传入创建的资源，用来初始化输出任务
		Output1 output1 = new Output1(r1);
		// 将输出任务放到线程中
		Thread t1 = new Thread(output1);
		// 开启输出线程
		t1.start();
	}

}

class Resource1 {
	public String name;
	public String sex;
	public boolean flag = false;
	public int i = 0;
	
}

class Input1 implements Runnable {
	private Resource1 r;
	Input1(Resource1 r) {
		this.r = r;
	}
	
	@Override
	public void run() {
		// set1(); // 线程安全代码
		// set2(); // 同步代码块
		set3(); // 唤醒机制
		
	}
	
	/**
	 * 问题： 产生线程安全问题的代码，如果赋值完name之后线程失去执行权，由输出线程执行，则name为当前赋值的新值，而sex则是上次赋值的结果
	 * 输入线程 输入丁东方 男，，线程失去执行权，输出线程获得执行权，输出丁东方男，然后输出线程失去执行权，
	 * 输入线程继续获得执行权，输入r.name = "丁丽丽"的时候失去执行权，输出线程则输出，丁丽丽男，导致出现问题。
	 * 解决思路：
	 * 	给输入线程的赋值代码加上同步代码块，不能使用同步函数的原因是同步函数的对象锁是this,而当前代码是对资源进行加锁，
	 * 	对应方法set2()
	 */
	public void set1() {
		while(true) {
			try{
				if(r.i % 2 == 0) {
					r.name = "丁东方_" + r.i; 
					Thread.sleep(100);
					r.sex = "男_" + r.i;
				} else {
					r.name = "丁丽丽_" + r.i;
					Thread.sleep(100);
					r.sex = "女" + r.i;
				}
				r.i ++;
				System.out.println(Thread.currentThread().getName() + ":输入:姓名:" + r.name + ", 性别:" + r.sex);
			} catch(InterruptedException e) {
				
			}
		}
	}
	
	/**
	 * set1代码问题解决思路：
	 * 	加上同步代码块，因为输入线程和输出线程都是对资源进行操作，资源是两个线程中所共享的对象，所以当前同步锁可以使用资源对象
	 * 
	 * 遗留问题：
	 * 	线程对资源进行重复操作，要么输入线程对资源进行赋值之后，输出线程没有输出，输入线程继续操作，导致资源不能一对一的赋值和输出。
	 * 解决思路
	 * 	当输入线程对资源进行赋值之后，如果输入线程再次获得执行权，则释放执行权，通知输出线程来输出，输出线程执行完之后，释放执行权，
	 * 	通知输入线程赋值，对应方法set3()
	 * 
	 */
	public void set2() {
		while(true) {
			synchronized (r) {
				try {
					if(r.i % 2 == 0) {
						r.name = "丁东方_" + r.i; 
						Thread.sleep(100);
						r.sex = "男" + r.i;
					} else {
						r.name = "丁丽丽_" + r.i;
						Thread.sleep(100);
						r.sex = "女" + r.i;
					}
					r.i ++;
					System.out.println(Thread.currentThread().getName() + ":输入:姓名:" + r.name + ", 性别:" + r.sex);
				} catch(InterruptedException e) {
					
				}
			}
		}
	}
	
	/**
	 * 使用flag来标记。输入线程判定flag为false时，对资源进行赋值，并将flag标记未true.同时唤醒输出线程。输入线程
	 * 再次获得执行权的时候，flag为true时，代表有资源未输出，则调用同步对象锁的wait方法释放执行权。
	 * 输出线程获得执行权的时候判定flag是否未true，如果未true，则需要对资源进行输出，否则释放执行权并唤醒输入线程
	 */
	public void set3() {
		while(true) {
			synchronized (r) {
				try {
					if(r.flag) {
						r.wait();
					}
					if(r.i % 2 == 0) {
						r.name = "丁东方_" + r.i; 
						Thread.sleep(100);
						r.sex = "男" + r.i;
					} else {
						r.name = "丁丽丽_" + r.i;
						Thread.sleep(100);
						r.sex = "女" + r.i;
					}
					r.i ++;
					System.out.println(Thread.currentThread().getName() + ":输入:姓名:" + r.name + ", 性别:" + r.sex);
					r.flag = true;
					r.notify();
				} catch(InterruptedException e) {
					
				}
			}
		}
	}
}

class Output1 implements Runnable {
	private Resource1 r;
	Output1(Resource1 r) {
		this.r = r;
	}
	
	@Override
	public void run() {
		// out1(); // 线程安全代码问题
		// out2(); // 同步代码块
		out3(); // 唤醒机制
	}
	
	public void out1() {
		while(true) {
			System.out.println(Thread.currentThread().getName() + ":输出:姓名:" + r.name + ", 性别:" + r.sex);
			System.out.println();
		}
	}
	
	public void out2() {
		while(true) {
			synchronized (r) {
				System.out.println(Thread.currentThread().getName() + ":输出:姓名:" + r.name + ", 性别:" + r.sex);
				System.out.println();
			}
		}
	}
	
	public void out3() {
		while(true) {
			synchronized (r) {
				try {
					if(!r.flag) {
						r.wait();
					}
					System.out.println(Thread.currentThread().getName() + ":输出:姓名:" + r.name + ", 性别:" + r.sex);
					System.out.println();
					r.flag = false;
					r.notify();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
