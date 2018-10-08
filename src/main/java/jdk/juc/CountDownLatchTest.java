package jdk.juc;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

;

/**
 * CountDownLatch ：闭锁，在完成某些运算是，只有其他所有线程的运算全部完成，当前运算才继续执行
 * CountDownLatch是通过一个计数器来实现的，计数器的初始值为线程的数量。每当一个线程完成了自己的任务后，
 * 计数器的值就会减1。当计数器值到达0时，它表示所有的线程已经完成了任务，然后在闭锁上等待的线程就可以恢复执行任务。
 * <p>
 *  确保某个计算在其需要的所有资源都被初始化之后才继续执行;
 *  确保某个服务在其依赖的所有其他服务都已经启动之后才启动;
 *  等待直到某个操作所有参与者都准备就绪再继续执行。
 */
public class CountDownLatchTest {

    /**
     * 每一个线程都操作一次list来放入值，最后由主线程来循环一共产生的数据
     */
    @Test
    public void testAddList() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(100);
        ListAdd ma = new ListAdd(latch);
        for (int i = 1; i <= 100; i++) {
            new Thread(ma, "线程" + i).start();
        }

        // 这个方法如果CountDownLatch初始的等待线程数量大于实际线程池数量，则会导致一直等待，进程不会结束
        latch.await();
        System.out.println("主线程取出数据,最终数据大小......................." + ListAdd.getLinkedList().size());
        // ListAdd.getLinkedList().forEach(System.out::println);
    }


    /**
     * 等待所有的线程都执行完成之后由主线程来计算消耗时间
     */
    @Test
    public void test1() {
        // 50代表需要等待50个线程数量，实际上就是闭锁需要等待的线程数量
        final CountDownLatch latch = new CountDownLatch(50);
        LatchDemo ld = new LatchDemo(latch);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            new Thread(ld).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
        }

        long end = System.currentTimeMillis();

        System.out.println("耗费时间为：" + (end - start));
    }


    /**
     * 创建线程，让线程去执行任务，线程的创建有先后顺序，所以理论上先创建的线程会比后创建的线程更容易获得执行权。
     * 现在要求必须所有的线程同时创建完成之后，然后发送一个指令才同时去竞争执行权
     */
    @Test
    public void atTheSameTimeStart() throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(500);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        LinkedList<Integer> elseNum = new LinkedList<>();
        for (int i = 0; i <= 500; i ++) {
        }
    }
}

class LatchDemo implements Runnable {

    private CountDownLatch latch;

    public LatchDemo(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 50000; i++) {
                if (i % 2 == 0) {
                    System.out.println(i);
                }
            }
        } finally {
            latch.countDown();
        }
    }
}

class ListAdd implements Runnable {
    private static LinkedList<String> linkedList = new LinkedList<>();
    private CountDownLatch latch;

    ListAdd(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            int random = new Random().nextInt(10000);
            System.out.println(Thread.currentThread().getName() + "放入：" + linkedList.size() + "-"+ random);
            linkedList.add(linkedList.size() + "-"+ random);
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    public static LinkedList<String> getLinkedList() {
        return linkedList;
    }
}

class SameTimeTask implements Callable<LinkedList<String>> {
    private volatile Integer totalTicketNum;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch beginCount;
    private LinkedList<String> linkedList;
    private volatile Integer currTicket;
    public SameTimeTask(Integer totalTicketNum, CountDownLatch beginCount, LinkedList<String> linkedList) {
        this.totalTicketNum = totalTicketNum;
        this.beginCount = beginCount;
        this.linkedList = linkedList;
    }

    @Override
    public LinkedList<String> call() {
        if (totalTicketNum == 0) {
            linkedList.add(Thread.currentThread().getName() + "没票了。。");
        }
        totalTicketNum --;
        currTicket ++;
        linkedList.add(Thread.currentThread().getName() + "抢到第" + currTicket + "张票，还剩" + totalTicketNum);
        return linkedList;
    }


    public synchronized void clear() {
        System.out.println(Thread.currentThread().getName() + "执行放行操作！");
        countDownLatch.countDown();
    }
}