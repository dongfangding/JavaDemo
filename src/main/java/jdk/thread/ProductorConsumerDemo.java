package main.java.jdk.thread;

/**
 * 此类紧接这NotifyDemo1的类，NotifyDemo1一个输入线程，一个输出线程，使用唤醒机制来一对一的输入和输出。
 * 进而延伸出此类，多生产多消费模式。多个输入线程和多个输出线程，共同执行。
 * @author Administrator
 */
public class ProductorConsumerDemo {
	public static void main(String args[]) {
		// 创建资源
		Resource2 r = new Resource2();
		// 传入创建的资源，用来初始化输入任务
		Input2 input = new Input2(r);
		// 将输入任务放到线程中
		Thread t0 = new Thread(input);
		// 开启输入线程
		t0.start();
		
		// 传入创建的资源，用来初始化输入任务
		Input2 input1 = new Input2(r);
		// 将输入任务放到线程中
		Thread t1 = new Thread(input1);
		// 开启输入线程
		t1.start();

		// 传入创建的资源，用来初始化输出任务
		Output2 output = new Output2(r);
		// 将输出任务放到线程中
		Thread t2 = new Thread(output);
		// 开启输出线程
		t2.start();
		
		Output2 output1 = new Output2(r);
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
class Resource2 {
	private String userName;
	private String sex;
	private boolean flag = false;
	private int index = 0;
	
	public int getIndex() {
		return index;
	}

	/**
	 * 此代码在多生产多消费模式中会产生如下两个问题
	 * 1. 数据不对应，重复生产或重复输出。原因如下
	 * 		1.1 当输入线程第一次获得执行权输入时，为丁东方0， 男0，判定this.flag为false,可以输入资源。之后随机唤醒线程，此时无线程出入沉睡状态
	 * 			假如此线程继续获得执行权，再次进入判定this.flag时，此时为true。于是第一个输入线程失去执行权，沉睡；
	 * 		1.2 如果此时第二个线程获得执行权，进入判定this.flag时为true,于是第二个输入线程也进入沉睡；
	 * 		1.3 此时，第一个输出线程获得执行权，判定this.flag为true,于是继续执行输出丁东方0，男0，线程继续获得执行权，判定this.flag
	 * 			为false,于是第一个输出线程沉睡。此时由第一个输入线程获得了执行权，于是，在this.wait处醒了过来，执行写入操作，之后线程
	 * 			将this.flag置为true，唤醒沉睡线程后，该线程重新判定this.flag为true,于是沉睡失去执行权；
	 * 		1.4 在1.3中沉睡的线程唤醒了第二个沉睡的输入线程，该线程在this.wait处醒来。此时，资源中已经存在资源，但是程序已经跳过了this.flag
	 * 			的检查，于是产生了重复生产，重复输出与此原理类似
	 * 2. 产生死锁问题，原因如下
	 * 		2.1 由于notify方法是对线程池中的沉睡线程做随机唤醒，在该例中，线程是否沉睡的条件为资源中是否存在数据为依据，
	 * 			假若第一个输入线程输入后，此时唤醒的是第二个输入线程，那么两个输入线程都会陷入沉睡状态。当第一个输出线程对资源进行输出时，
	 * 			正好此时该线程唤醒的不是输入线程，正好是第二个输出线程，那么所有的线程都会陷入沉睡状态，于是造成死锁
	 * 		2.2 但是由于是if判断，如果再第一个输入线程沉睡后，第二个输入线程沉睡时，唤醒的是第一个输入线程，那么该线程在this.wait处
	 * 			醒来，程序不会再判断flag。于是程序继续执行，此步则是输出重复的问题。很难有理想的情况下来看到死锁现象。所以，需要在程序
	 * 			醒来之后，重复去判断flag。改为while。则，很容易看到死锁现象。
	 * @param userName
	 * @param sex
	 */
	public synchronized void set3(String userName, String sex) {
		try {
			if (this.flag) {
				this.wait();
			}
			// 死锁代码
			/*while(this.flag) {
				this.wait();
			}*/
			this.sex = sex;
			this.index ++;
			this.userName = userName+this.index;
			System.out.println(Thread.currentThread().getName() + "---count: "
					+"输入：姓名:" + this.userName + "--------性别:" + sex);
			this.flag = true;
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解决方案
	 * 		让线程醒来之后继续判断标识，并且每次唤醒其中一个线程后，该线程都要调用notifyAll()来唤醒所有线程。
	 * 弊端：
	 * 		如果唤醒了本方线程，即输入线程唤醒输入线程，是在浪费时间，输出线程唤醒了输出线程，也是浪费时间。
	 * 		不能点到点的唤醒对方线程。
	 * @param userName
	 * @param sex
	 */
	public synchronized void set4(String userName, String sex) {
		try {
			while (this.flag) {
				this.wait();
			}
			this.sex = sex;
			this.index ++;
			this.userName = userName+this.index;
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName() + "---count: "
					+ "输入：姓名:" + this.userName + "--------性别:" + this.sex);
			this.flag = true;
			this.notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void out3() {
		try {
			if (!this.flag) {
				this.wait();
			}
			// 死锁代码
			/*while(!this.flag) {
				this.wait();
			}*/
			System.out.println(Thread.currentThread().getName() + "---count: "
					+"输出：姓名:" + userName + "--------性别:" + sex + "\n");
			this.flag = false;
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void out4() {
		try {
			while (!this.flag) {
				this.wait();
			}
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName() + "---count: "
					+ "输出：姓名:" + userName + "--------性别:" + sex + "\n");
			this.flag = false;
			this.notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Input2 implements Runnable {
	private Resource2 r2;
	
	Input2(Resource2 r2) {
		this.r2 = r2;
	}

	@Override
	public void run() {
		while (true) {
			r2.set3("丁东方", "男");
			// r2.set4("丁东方", "男");
		}
	}
}

class Output2 implements Runnable {
	private Resource2 r;

	Output2(Resource2 r) {
		this.r = r;
	}

	@Override
	public void run() {
		while (true) {
			// r.out3();
			r.out4();
		}
	}
}
