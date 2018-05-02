package jdk.juc;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DDf on 2018/5/2
 * http://www.importnew.com/22007.html
 * 当期望许多线程访问一个给定 collection 时，ConcurrentHashMap 通常优于同步的 HashMap，
 * ConcurrentSkipListMap 通常优于同步的 TreeMap。当期望的读数和遍历远远
 * 大于列表的更新数时，CopyOnWriteArrayList 优于同步的 ArrayList。
 * 但他们的共性都是弱一致性，不能保证当前线程能够读取到另外一个线程put的最新值
 */
public class ConcurrentHashMapTest {

    @Test
    public void testNotSync() {
        NotSyncMapTask mapTask = new NotSyncMapTask();
        for (int i = 1; i <= 10; i++) {
            new Thread(mapTask).start();
        }
    }


    @Test
    public void testSync() {
        SyncMapTask mapTask = new SyncMapTask();
        for (int i = 1; i <= 10; i++) {
            new Thread(mapTask).start();
        }
    }


    @Test
    public void testNormalMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("AAA", "AAA");
        map.put("BBB", "BBB");
        map.put("CCC", "CCC");
        map.forEach((k, v) -> System.out.println(k + ":" + v));
    }

}

class NotSyncMapTask implements Runnable {
    private static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    static {
        map.put("AAA", "AAA");
        map.put("BBB", "BBB");
        map.put("CCC", "CCC");
    }

    @Override
    public void run() {
        int random = new Random().nextInt();
        System.out.println(Thread.currentThread().getName() + "+++++++++++++++++++: " + map.size());
        for (Map.Entry<String, Object> m : map.entrySet()) {
            System.out.println(Thread.currentThread().getName() + "取出," + m.getKey() + ":" + m.getValue());
            System.out.println(Thread.currentThread().getName() + "存入," + random + ":" + random);
            map.put(random + "", random);
        }
    }
}


class SyncMapTask implements Runnable {
    private static Map<String, Object> map = Collections.synchronizedMap(new HashMap<>());
    private Lock lock = new ReentrantLock();

    static {
        map.put("AAA", "AAA");
        map.put("BBB", "BBB");
        map.put("CCC", "CCC");
    }

    @Override
    public void run() {
        lock.lock();
        String random = new Random().nextInt() + "";
        try {
            System.out.println("before............");
            for (Map.Entry<String, Object> m : map.entrySet()) {
                System.out.print(Thread.currentThread().getName() + "取出: " + m.getKey() + ":" + m.getValue());
            }

            /**
             * 不晓得这一行为什么会失效，如果不再线程任务中，却是可以正常循环出来，这里如果使用了forEach，
             * 看起来会直接终止所有的线程任务一样
             *
             */
            // map.forEach((k, v) -> System.out.println(Thread.currentThread().getName() + "取出" + k + "v"));
            System.out.println(",存入" + random);
            map.put(random, random);
        } finally {
            lock.unlock();
        }
    }
}