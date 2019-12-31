package guava.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * $
 * <p>
 * <p>
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 * O\ = /O
 * ___/`---'\____
 * .   ' \\| |// `.
 * / \\||| : |||// \
 * / _||||| -:- |||||- \
 * | | \\\ - /// | |
 * | \_| ''\---/'' | |
 * \ .-\__ `-` ___/-. /
 * ___`. .' /--.--\ `. . __
 * ."" '< `.___\_<|>_/___.' >'"".
 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * .............................................
 * 佛曰：bug泛滥，我已瘫痪！
 *
 * @author dongfang.ding
 * @date 2019/12/31 0031 10:21
 */
public class LoadingCacheDemo1 {
    private static Map<String, String> dataMap = new HashMap<>();

    private static LoadingCache<String, String> loadingCache;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50), new ThreadFactoryBuilder().setNameFormat("executor-pool").build());

    static {
        dataMap.put("1","第一个对象");
        dataMap.put("2", "第二个对象");
        dataMap.put("3", "第三个对象");

        loadingCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .recordStats()
            .refreshAfterWrite(5, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    return get(key);
                }

                @Override
                public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                    // 如果使用异步的话，短期内多次获取，因为此时异步尚未执行结束，大概率会造成获取的时候使用的还是旧的缓存值
                    ListenableFutureTask<String> task = ListenableFutureTask.create(() -> get(key));
                    executor.execute(task);

                    // 如果想要在执行刷新的时候强制等待新值，可以使用闭锁功能来强制等待
                    task.get();
                    return task;
                }
            });
    }


    public static String get(String key) {
        try {
            // 模拟向数据库中取数据需要耗时1000毫秒
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dataMap.get(key);
    }

    public static String loadIfPresent(String key) {
        return loadingCache.getUnchecked(key);
    }

    public static void main(String[] args) throws InterruptedException {

        long before = System.currentTimeMillis();

        System.out.println(get("1"));

        long after = System.currentTimeMillis();

        System.out.println("耗时: " + (after - before));




        long before2 = System.currentTimeMillis();

        System.out.println(loadIfPresent("1"));

        long after2 = System.currentTimeMillis();

        System.out.println("耗时: " + (after2 - before2));


        dataMap.put("1", "第一个对象被更改了!");

        long before3 = System.currentTimeMillis();

        System.out.println(get("1"));

        long after3 = System.currentTimeMillis();

        System.out.println("耗时: " + (after3 - before3));




        long before4 = System.currentTimeMillis();

        System.out.println(loadIfPresent("1"));

        long after4 = System.currentTimeMillis();

        System.out.println("耗时: " + (after4 - before4));

        Thread.sleep(6000);



        long before5 = System.currentTimeMillis();

        System.out.println(loadIfPresent("1"));

        long after5 = System.currentTimeMillis();

        System.out.println("耗时: " + (after5 - before5));


        Thread.sleep(2000);

        long before6 = System.currentTimeMillis();

        System.out.println(loadIfPresent("1"));

        long after6 = System.currentTimeMillis();

        System.out.println("耗时: " + (after6 - before6));



        long before7 = System.currentTimeMillis();

        System.out.println(loadIfPresent("1"));

        long after7 = System.currentTimeMillis();

        System.out.println("耗时: " + (after7 - before7));


        System.out.println("=========================================");
        System.out.println(loadingCache.stats());


        executor.shutdown();

    }

}
