package main.java.jdk.thread;

/**
 * 线程的唤醒机制 需要使用唤醒的应用场景是： 创建资源， 创建两个线程，同时操作资源，一个输入线程用来对数据进行赋值，
 * 赋值结束之后，立马切换线程到输出线程，对资源进行输出，然后再将执行权释放，唤醒输入线程。 实现每输入一个数据，立马输出这个数据，然后才进行赋值
 * 
 * wait和sleep的区别
 * 	1. wait可以指定时间，也可以不指定时间， sleep必须指定时间。
 *  2. 在同步中，对cpu的执行权和锁的处理不同
 *  	wait释放执行权，释放锁
 *  	sleep 释放执行权，不释放锁。
 * @author Administrator
 */
public class NotifyDemo {
	public static void main(String args[]) {
		// 创建资源
		Resource r = new Resource();
		// 传入创建的资源，用来初始化输入任务
		Input input = new Input(r);
		// 将输入任务放到线程中
		Thread t0 = new Thread(input);
		// 开启输入线程
		t0.start();

		// 传入创建的资源，用来初始化输出任务
		Output output = new Output(r);
		// 将输出任务放到线程中
		Thread t1 = new Thread(output);
		// 开启输出线程
		t1.start();
	}
}

/**
 * 资源类 输入和输出方法，对方法进行枷锁，使用同步函数
 * 重点是，wait和notify方法，只能对当前对象锁生效，如果存在多个对象锁，则另外一个对象锁的方法无法对当前 线程产生影响。
 * 
 * @author Administrator
 *
 */
class Resource {
	private String userName;
	private String sex;
	private boolean flag = false;
	private int index = 0;

	public int getIndex() {
		return index;
	}

	/**
	 * 问题代码：
	 * 资源的set方法。此代码会产生安全问题，没有加同步的话，如果在第一次执行this.userName=userName结束之后，赋值丁丽丽，线程失去执行权，
	 * 则输出线程输出丁丽丽,而 sex为Null；另外如果不是第一次执行，在此前已录入信息丁东方男，则输出上次赋值的值，丁丽丽男；
	 * 
	 * @param userName
	 * @param sex
	 */
	public void set1(String userName, String sex) {
		try {
			this.userName = userName;
			this.index ++;
			Thread.sleep(100);
			this.sex = sex;
			System.out.println(Thread.currentThread().getName() + "输入：姓名:" + userName + "--------性别:" + sex);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 问题代码: 使用同步函数锁来对赋值动作进行加锁， 同步函数锁对象为this，资源对象为共享数据，正好也是当前this对象，保证了对象锁一致。
	 * 解决了，赋值动作赋值一半时输出线程不会输出资源的问题。 新问题：
	 * 输入与输出资源不对应，输入线程连续获得执行权将连续输出相同资源。应保证输入数据与输出数据一一对应。
	 */
	public synchronized void set2(String userName, String sex) {
		try {
			this.userName = userName;
			this.index ++;
			Thread.sleep(100);
			this.sex = sex;
			System.out.println(Thread.currentThread().getName() + "输入：姓名:" + userName + "--------性别:" + sex);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 等待唤醒机制： 通过同步函数来对赋值线程进行枷锁，当赋值结束之后，改变标志，代表资源已经被赋值，调用
	 * 对象锁的wait方法让线程沉睡，释放该资源的执行权, 然后调用当前对象锁的notify方法，来随机唤醒线程。
	 * 如果唤醒了输入线程，则继续等待；直到唤醒了输出线程，则程序正常输出。
	 * @param userName
	 * @param sex
	 */
	public synchronized void set3(String userName, String sex) {
		try {
			if (this.flag) {
				this.wait();
			}
			this.userName = userName;
			this.index ++;
			Thread.sleep(100);
			this.sex = sex;
			System.out.println(Thread.currentThread().getName() + "输入：姓名:" + userName + "--------性别:" + sex);
			this.flag = true;
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void out1() {
		try {
			System.out.println(Thread.currentThread().getName() + "输出：姓名:" + userName + "--------性别:" + sex + "\n");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void out2() {
		try {
			System.out.println(Thread.currentThread().getName() + "输出：姓名:" + userName + "--------性别:" + sex + "\n");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 等待唤醒机制： 通过同步函数来对输出线程进行枷锁，输出线程获得执行权之后，收线判断flag是否存在资源，如果没有资源，则让线程沉睡，释放执行权。
	 * 如果存在资源，则输出资源，输出后改变flag为不存在资源，然后唤醒沉睡线程。
	 * 如果唤醒了本身，输出线程，则判断flag不通过，继续沉睡。直到唤醒输入线程，然后输入资源。
	 */
	public synchronized void out3() {
		try {
			if (!this.flag) {
				this.wait();
			}
			System.out.println(Thread.currentThread().getName() + "输出：姓名:" + userName + "--------性别:" + sex + "\n");
			Thread.sleep(100);
			this.flag = false;
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Input implements Runnable {
	private Resource r;

	Input(Resource r) {
		this.r = r;
	}

	@Override
	public void run() {
		while (true) {
			if (r.getIndex() % 2 == 0) {
				// 线程安全问题代码
				// r.set1("丁东方" + r.getIndex(), "----男" + r.getIndex());
				// 同步函数锁（this对象）
				// r.set2("丁东方" + r.getIndex(), "----男" + r.getIndex());
				// 等待唤醒机制
				r.set3("丁东方" + r.getIndex(), "----男" + r.getIndex());
			} else {
				// r.set1("丁丽丽" + r.getIndex(), "----女" + r.getIndex());
				// r.set2("丁丽丽" + r.getIndex(), "----女" + r.getIndex());
				r.set3("丁丽丽" + r.getIndex(), "----女" + r.getIndex());
			}
		}
	}
}

class Output implements Runnable {
	private Resource r;

	Output(Resource r) {
		this.r = r;
	}

	@Override
	public void run() {
		while (true) {
			// 线程安全问题代码
			// r.out1();
			// 同步函数锁（this对象）
			// r.out2();
			// 等待唤醒机制
			r.out3();
		}
	}
}
