package jdk.juc;

/**
 * 死锁的产生以及排查步骤
 * 1. 产生，两个或两个以上的线程各自持有对方所需要尝试获得的锁，因为各自持有并不会释放，而对方尝试获得却又得不到两个互相等待，最终产生死锁
 *
 * 2. 排查故障
 *    2.1 使用jps -l 命令查看所有运行的java类，会打印pid以及运行的主类,如下所示
 *      6016 org.jetbrains.idea.maven.server.RemoteMavenServer
 *      8944 sun.tools.jps.Jps
 *      6696 jdk.juc.Deadlock
 *
 *    2.2 使用jstack -pic命令来查看对应Pid栈信息,缩略版如下，可以看到waiting和Lock的锁对象
 *
 * Java stack information for the threads listed above:
 * ===================================================
 * "Thread-1":
 *         at jdk.juc.Deadlock.task(Deadlock.java:26)
 *         - waiting to lock <0x00000007807ee6f8> (a java.lang.Object)
 *         - locked <0x00000007807ee708> (a java.lang.Object)
 *         at jdk.juc.Deadlock.lambda$main$1(Deadlock.java:39)
 *         at jdk.juc.Deadlock$$Lambda$2/1637506559.run(Unknown Source)
 *         at java.lang.Thread.run(Thread.java:745)
 * "Thread-0":
 *         at jdk.juc.Deadlock.task(Deadlock.java:26)
 *         - waiting to lock <0x00000007807ee708> (a java.lang.Object)
 *         - locked <0x00000007807ee6f8> (a java.lang.Object)
 *         at jdk.juc.Deadlock.lambda$main$0(Deadlock.java:35)
 *         at jdk.juc.Deadlock$$Lambda$1/1161082381.run(Unknown Source)
 *         at java.lang.Thread.run(Thread.java:745)
 *
 *
 *
 *
 * @author DDf on 2019/4/14
 */
public class Deadlock {

	private final Object lock1;
	private final Object lock2;

	Deadlock(Object lock1, Object lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}

	public void task() {
		synchronized (lock1) {
			System.out.println(Thread.currentThread().getName() + "持有锁[" + lock1 + "],尝试获得锁[" + lock2 + "]");
			try {
				// 故意睡一会，使得获得第一个锁之后不能立即获得第二个锁,让第二个线程来拿到第二个锁，从而导致死锁
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (lock2) {
				System.out.println(Thread.currentThread().getName() + "持有锁[" + lock2 + "]");
			}
		}
	}

	public static void main(String[] args) {
		Object obj = new Object();
		Object obj1 = new Object();
		new Thread(() -> new Deadlock(obj, obj1).task()).start();
		new Thread(() -> new Deadlock(obj1, obj).task()).start();
	}
}
