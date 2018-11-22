package jdk.juc;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;;

public class TestForkJoinPool {

    @Test
    public void test() {
        Instant start = Instant.now();

        ForkJoinPool pool = new ForkJoinPool();

        ForkJoinTask<Long> task = new ForkJoinSumCalculate(0L, 50000000000L);

        Long sum = pool.invoke(task);

        System.out.println(sum);

        Instant end = Instant.now();

        System.out.println("耗费时间为：" + Duration.between(start, end).toMillis());//166-1996-10590
    }

    @Test
    public void test1() {
        Instant start = Instant.now();

        long sum = 0L;

        for (long i = 0L; i <= 50000000000L; i++) {
            sum += i;
        }

        System.out.println(sum);

        Instant end = Instant.now();

        System.out.println("耗费时间为：" + Duration.between(start, end).toMillis());//35-3142-15704
    }

    //java8 新特性
    @Test
    public void test2() {
        Instant start = Instant.now();

        Long sum = LongStream.rangeClosed(0L, 50000000000L)
                .parallel()
                .reduce(0L, Long::sum);

        System.out.println(sum);

        Instant end = Instant.now();

        System.out.println("耗费时间为：" + Duration.between(start, end).toMillis());//1536-8118
    }


    /**
     * 无返回值的Fork/Join示例
     */
    @Test
    public void test3() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() / 2);
        Object object = new Object();
        ReadExcelAction readExcelAction = new ReadExcelAction(object, 0L, 140000L);
        forkJoinPool.invoke(readExcelAction);

        System.out.println(Runtime.getRuntime().availableProcessors());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ForkJoinSumCalculate extends RecursiveTask<Long> {

    /**
     *
     */
    private static final long serialVersionUID = -259195479995561737L;

    private long start;
    private long end;

    private static final long THURSHOLD = 1000L;  //临界值

    public ForkJoinSumCalculate(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long length = end - start;

        if (length <= THURSHOLD) {
            long sum = 0L;

            for (long i = start; i <= end; i++) {
                sum += i;
            }

            return sum;
        } else {
            long middle = (start + end) / 2;

            ForkJoinSumCalculate left = new ForkJoinSumCalculate(start, middle);
            left.fork(); //进行拆分，同时压入线程队列

            ForkJoinSumCalculate right = new ForkJoinSumCalculate(middle + 1, end);
            right.fork(); //

            return left.join() + right.join();
        }
    }

}


/**
 * 无返回值的Fork/Join示例
 * 如读取Excel，每行之间互不影响，则可以使用让线程同时读不同区间的行号
 */
class ReadExcelAction extends RecursiveAction {

    private long start;
    private long end;
    // 具体要做的任务类
    private Object object;

    private static final long THURSHOLD = 10;

    public ReadExcelAction(Object object, long start, long end) {
        this.object = object;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if ((end - start) < THURSHOLD) {
            for (long i = start; i <= end; i++) {
                System.out.println(Thread.currentThread().getName() + "读取行号： " + i);
                object.hashCode();
            }
        } else {
            long middle = (end + start) / 2;

            ReadExcelAction left = new ReadExcelAction(object, start, middle);
            left.fork();

            ReadExcelAction right = new ReadExcelAction(object, middle + 1, end);
            right.fork();
        }
    }
}


