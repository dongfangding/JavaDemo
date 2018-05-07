package jdk.juc;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWriteArrayList/CopyOnWriteArraySet : “写入并复制”
 * 注意：添加操作多时，效率低，因为每次添加时都会进行复制，开销非常的大。并发迭代操作多时可以选择。
 * <p>
 * 线程5取出: AA
 * 线程5存入: AA
 * 线程10取出: AA
 * 线程10存入: AA
 * 线程10取出: BB
 * 线程10存入: AA
 * 线程10取出: CC
 * 线程10存入: AA
 * 线程9取出: AA
 * 线程2取出: AA
 * 线程2存入: AA
 * 线程2取出: BB
 * 线程6取出: AA
 * 线程6存入: AA
 * 线程6取出: BB
 * 线程6存入: AA
 * 线程3取出: AA
 * 线程3存入: AA
 * 线程3取出: BB
 * 线程3存入: AA
 * 线程3取出: CC
 * 线程3存入: AA
 * 线程4取出: AA
 * 线程4存入: AA
 * 线程8取出: AA
 * 线程8存入: AA
 * 线程8取出: BB
 * 线程7取出: AA
 * 线程7存入: AA
 * 线程7取出: BB
 * 线程7存入: AA
 * 线程7取出: CC
 * 线程7存入: AA
 * 线程1取出: AA
 * 线程1存入: AA
 * 线程1取出: BB
 * 线程1存入: AA
 * 线程1取出: CC
 * 线程1存入: AA
 * 线程8存入: AA
 * 线程4取出: BB
 * 线程4存入: AA
 * 线程4取出: CC
 * 线程4存入: AA
 * 线程6取出: CC
 * 线程6存入: AA
 * 线程2存入: AA
 * 线程9存入: AA
 * 线程9取出: BB
 * 线程9存入: AA
 * 线程9取出: CC
 * 线程9存入: AA
 * 线程5取出: BB
 * 线程5存入: AA
 * 线程5取出: CC
 * 线程5存入: AA
 * 线程2取出: CC
 * 线程2存入: AA
 * 线程8取出: CC
 * 线程8存入: AA
 * <p>
 * CopyOnWriteArrayList使用了一种叫写时复制的方法，当有新元素添加到CopyOnWriteArrayList时，先从原有的数组中拷贝一份出来，然后在新的数组做写操作，写完之后，再将原来的数组引用指向到新数组。
 * <p>
 * 当有新元素加入的时候，如下图，创建新数组，并往新数组中加入一个新元素,这个时候，array这个引用仍然是指向原数组的。
 * 当元素在新数组添加成功后，将array这个引用指向新数组。
 * <p>
 * 由于所有的写操作都是在新数组进行的，这个时候如果有线程并发的写，则通过锁来控制，如果有线程并发的读，则分几种情况：
 * 1、如果写操作未完成，那么直接读取原数组的数据；
 * 2、如果写操作完成，但是引用还未指向新数组，那么也是读取原数组数据；
 * 3、如果写操作完成，并且引用已经指向了新的数组，那么直接从新数组中读取数据。
 * <p>
 * 通过上面的分析，CopyOnWriteArrayList 有几个缺点：
 * 1、由于写操作的时候，需要拷贝数组，会消耗内存，如果原数组的内容比较多的情况下，可能导致young gc或者full gc
 * <p>
 * 2、不能用于实时读的场景，像拷贝数组、新增元素都需要时间，所以调用一个set操作后，读取到数据可能还是旧的,虽然CopyOnWriteArrayList 能做到最终一致性,但是还是没法满足实时性要求；
 * <p>
 * CopyOnWriteArrayList 合适读多写少的场景，不过这类慎用
 * 因为谁也没法保证CopyOnWriteArrayList 到底要放置多少数据，万一数据稍微有点多，每次add/set都要重新复制数组，这个代价实在太高昂了。在高性能的互联网应用中，这种操作分分钟引起故障。
 */
public class TestCopyOnWriteArrayList {

    public static void main(String[] args) {
        HelloThread ht = new HelloThread();

        for (int i = 1; i <= 10; i++) {
            new Thread(ht, "线程" + i).start();
        }
    }

}

class HelloThread implements Runnable {

//	private static List<String> list = Collections.synchronizedList(new ArrayList<String>());

    private static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

    static {
        list.add("AA");
        list.add("BB");
        list.add("CC");
    }

    @Override
    public void run() {

        Iterator<String> it = list.iterator();

        while (it.hasNext()) {
            System.out.println(Thread.currentThread().getName() + "取出: " + it.next());
            System.out.println(Thread.currentThread().getName() + "存入: AA");
            // 如果同一个线程连续获得执行权，取出AA之后再放入AA，并不一定能保证可以读取到最新的数组，
            // 所以会出现同一个线程并不能连续两次获得AA，感觉这个类没什么卵用
            list.add("AA");
        }
    }

}