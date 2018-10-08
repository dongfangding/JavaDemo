package jdk.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author DDf on 2018/10/8
 *
 *
 * 模拟一个类中需要调用多个接口来进行判断是否错误之类的信息，如果并行调用，则如果前面几个接口都正确，而最后一个接口
 * 出错，则无疑会浪费时间。可以将多个接口封装成线程任务并行执行，然后主线程来判断返回结果
 *
 */
public class TaskParallelExecute {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Flag flag = new Flag();
        CountDownLatch countDownLatch = new CountDownLatch(3);
        Thread1 thread1 = new Thread1(flag, countDownLatch);
        Thread2 thread2 = new Thread2(flag, countDownLatch);
        Thread3 thread3 = new Thread3(flag, countDownLatch);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(thread1::get);
        executorService.submit(thread2::get);
        executorService.submit(thread3::get);

        // 这种方式如果第一个接口就返回false，那么则时间和多个任务并行时间相差不大，但返回false的接口越在后面越浪费时间
       /* thread1.get();
        thread2.get();
        thread3.get();*/
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程执行：" + flag.isFlag());
        long end = System.currentTimeMillis();
        System.out.println("总共耗时： " + (end - start));
        executorService.shutdown();
    }
}

/**
 * 代表调用的接口是否执行成功，有一个失败则算失败，失败之后不能被另外一个成功的接口覆盖，
 * 如果需要一个标志来代表每个接口自己调用是否成功，可以继续在自己的线程类中增加一个属性来代表，并暴露get方法供其他使用
 */
class Flag {
    private volatile boolean flag = true;

    public boolean isFlag() {
        return flag;
    }

    public synchronized void setFlag(boolean flag) {
        if (this.flag) {
            this.flag = flag;
        }
    }
}

class Thread1 {
    private Flag flag;
    private CountDownLatch countDownLatch;
    Thread1(Flag flag, CountDownLatch countDownLatch) {
        this.flag = flag;
        this.countDownLatch = countDownLatch;
    }

    public void get() {
        try {
            System.out.println("Thread1: " + flag.isFlag());
            if (flag.isFlag()) {
                Thread.sleep(5000);
                System.out.println("Thread1............");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}

class Thread2 {
    private Flag flag;
    private CountDownLatch countDownLatch;
    Thread2(Flag flag, CountDownLatch countDownLatch) {
        this.flag = flag;
        this.countDownLatch = countDownLatch;
    }

    public void get() {
        try {
            System.out.println("Thread2: " + flag.isFlag());
            if (flag.isFlag()) {
                Thread.sleep(5000);
                System.out.println("Thread2............");
                flag.setFlag(1==1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}

class Thread3 {
    private Flag flag;
    private CountDownLatch countDownLatch;
    Thread3(Flag flag, CountDownLatch countDownLatch) {
        this.flag = flag;
        this.countDownLatch = countDownLatch;
    }

    public void get() {
        try {
            System.out.println("Thread3: " + flag.isFlag());
            if (flag.isFlag()) {
                Thread.sleep(5000);
                System.out.println("Thread3............");
                flag.setFlag(1 != 1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
