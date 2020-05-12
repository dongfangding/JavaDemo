package jdk.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>不依赖对象锁的等待唤醒机制</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/05/12 11:51
 */
public class ParkUnParkDemo {

    public static void main(String[] args) {

        AtomicInteger atomicInteger = new AtomicInteger(0);


        Thread thread = new Thread(() -> {
            while (true) {
                if (atomicInteger.get() == 0) {
                    System.out.println("park-------------------");
                    LockSupport.park();
                } else {
                    try {
                        System.out.println("有数据了，程序继续");
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        new Thread(() -> {
            int i = 0;
            while (true) {
                if (i > 10) {
                    atomicInteger.set(1);
                } else {
                    i ++;
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                        System.out.println("唤醒线程1， 尝试获取最新值");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LockSupport.unpark(thread);
            }
        }).start();

    }
}
