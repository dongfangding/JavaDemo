package jdk.thread;

/**
 * 线程安全问题产生的原因
 * 1. 多个线程操作共享的数据
 * 2. 操作共享数据的代码有多行
 * 当一个线程在执行操作共享数据的多条代码过程中，其它线程参与了运算，就会导致线程安全问题的产生
 * <p>
 * 解决思路：
 * 就是将参与线程共享数据的代码封装起来，当有线程在运算这些代码时，其它线程不可参与进来。当
 * 该线程操作完成之后，其他线程才可以进入线程任务代码
 * 在java中，可以使用同步代码块来解决这个问题
 * synchronized(对象) {
 * // 对象可以是任意的，但是需要保证所有的需要同步的同步代码块使用的是相同的对象锁
 * }
 * <p>
 * 同步的好处：
 * 解决了线程的安全问题
 * 同步的弊端：
 * 相对降低了效率，因为同步外的线程代码都会判断同步锁
 * 同步的前提：
 * 必须有多个线程并且使用同一个锁
 *
 * @author DingDongfang
 */
public class SynchronizedFunctionLock {
    public static void main(String args[]) {
        Ticket1 t = new Ticket1();
        // t和线程中的this.是相等的。
        System.out.println("-----------------t:" + t + "---------------------");
        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        t1.start();
        t2.start();
    }
}

class Ticket1 implements Runnable {
    private int num = 100;

    /**
     * 使用同步函数锁。同步函数的锁使用的是this。
     */
    public synchronized void sale() {
        System.out.println("----------------" + this + "-------------------");
        while (true) {
            if (num > 0) {
                try {
                    Thread.sleep(10);
                    num--;
                    System.out.println(Thread.currentThread().getName() + ",剩余票:" + num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }
    }

    @Override
    public void run() {
        sale();
    }
}