package guava.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jdk.reflect.StringUtil;

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

    /**
     * 原始数据集合
     */
    private final static Map<String, String> DATA_MAP;

    /**
     * 采用懒加载的缓存，只有当获取对应的key时才进行第一次的获取然后进行缓存
     */
    private static LoadingCache<String, String> LAZY_LOADING_CACHE;

    /**
     * 维护某些不需要刷新的缓存key
     */
    private final static ImmutableSet<String> NEVER_REFRESH_KEY = ImmutableSet.of("2", "3");

    /**
     * 初始化直接缓存全部资源， 本地缓存失效后，重新缓存全部数据
     */
    private static LoadingCache<String, String> INIT__LOADING_CACHE;

    /**
     * reload线程池
     */
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500), new ThreadFactoryBuilder().setNameFormat("reload-executor-pool").build());

    static {
        DATA_MAP = new HashMap<>();
        DATA_MAP.put("1","第一个对象");
        DATA_MAP.put("2", "第二个对象");
        DATA_MAP.put("3", "第三个对象");

        initLazyCache();
        initInitCache();

    }


    public static void main(String[] args) throws InterruptedException {
        // 演示基本获取， 数据原始集合更新的情况下，缓存未及时更新会存在数据不一致的问题
        lazyInit();
        // 初始化加载全部缓存
        initLoad();
    }

    /**
     * 初始化懒加载的LoadingCache
     */
    private static void initLazyCache() {
        LAZY_LOADING_CACHE = CacheBuilder.newBuilder()
                .maximumSize(500)
                .recordStats()
                .refreshAfterWrite(3, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {

                    /**
                     * load负责当缓存不存在时，如何加载值进缓存
                     * @param key
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public String load(String key) throws Exception {
                        if (StringUtil.isBlank(key)) {
                            return null;
                        }
                        // 只重新获取并缓存当前要获取的key
                        return get(key);
                    }

                    /**
                     * 默认调用的就是load方法，所以load方法需要实现如果取不到值的情况下，重新加载缓存。
                     * 该方法会在refreshAfterWrite缓存过期的时候被调用
                     *
                     * 其实这里重写的最大意义就是将重新加载的代码给异步了，如果不需要的话，没有必要重写
                     * @param key
                     * @param oldValue
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        // 有些键不需要刷新，并且我们希望刷新是异步完成的, 如果不需要刷新直接返回旧值，如果需要刷新则调用load方法重新缓存
                        if (neverNeedsRefresh(key)) {
                            return Futures.immediateFuture(oldValue);
                        } else {
                            // 如果使用异步的话，短期内多次获取，因为此时异步尚未执行结束，大概率会造成获取的时候使用的还是旧的缓存值
                            ListenableFutureTask<String> task = ListenableFutureTask.create(() -> load(key));
                            EXECUTOR.execute(task);
                            // 可选使用闭锁来让获取缓存的线程强制等待缓存加载完成使用最新值, 需要看load方法的执行时间以及对缓存不一致的接受程度自行决定
//                        task.get();
                            return task;
                        }
                    }
                });
    }

    /**
     * 初始化启动先加载全部缓存的LoadingCache
     */
    private static void initInitCache() {
        INIT__LOADING_CACHE = CacheBuilder.newBuilder()
                .maximumSize(500)
                .recordStats()
                .refreshAfterWrite(3, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {
                    /**
                     * load负责当缓存不存在时，如何加载值进缓存
                     * @param key
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public String load(String key) throws Exception {
                        if (StringUtil.isBlank(key)) {
                            return null;
                        }
                        return get(key);
                    }

                    /**
                     * 默认调用的就是load方法，所以load方法需要实现如果取不到值的情况下，重新加载缓存。
                     * 该方法会在refreshAfterWrite缓存过期的时候被调用
                     *
                     * 其实这里重写的最大意义就是将重新加载的代码给异步了，如果不需要的话，没有必要重写
                     * @param key
                     * @param oldValue
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        // 有些键不需要刷新，并且我们希望刷新是异步完成的, 如果不需要刷新直接返回旧值，如果需要刷新则调用load方法重新缓存
                        if (neverNeedsRefresh(key)) {
                            return Futures.immediateFuture(oldValue);
                        } else {
                            // 如果使用异步的话，短期内多次获取，因为此时异步尚未执行结束，大概率会造成获取的时候使用的还是旧的缓存值
                            ListenableFutureTask<String> task = ListenableFutureTask.create(() -> load(key));
                            EXECUTOR.execute(task);
                            return task;
                        }
                    }
                });
        // 这里也可以直接初始化缓存，就不用懒加载获取某个key的时候再去缓存，但是reload最好还是只缓存自己的key，
        // 否则在失效的一瞬间大量的key被访问，会同时重复缓存
        DATA_MAP.forEach(INIT__LOADING_CACHE::put);
    }

    /**
     * 判断缓存是否需要刷新
     * @param key
     * @return
     */
    private static boolean neverNeedsRefresh(String key) {
        return NEVER_REFRESH_KEY.contains(key);
    }

    /**
     * 模拟获取原始数据
     * @param key
     * @return
     */
    public static String get(String key) {
        try {
            // 模拟向数据库中取数据需要耗时1000毫秒
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DATA_MAP.get(key);
    }

    public static String loadIfPresent(LoadingCache<String, String> loadingCache, String key) {
        return loadingCache.getUnchecked(key);
    }


    /**
     * 采用懒加载的方式加载缓存，当某个key被访问的时候才开始第一次获取并缓存，
     * 所以再缓存后到未过期之间的这段时间，第一次获取时耗时最长，后面会直接从缓存中获取
     *
     * 以及可能存在的缓存不一致、异步加载、缓存重新刷新、特定值不更新缓存等特性
     * @throws InterruptedException
     */
    private static void lazyInit() throws InterruptedException {
        logTimeGet(LAZY_LOADING_CACHE, "1");

        // 这里虽然修改了存储的值，但是由于缓存写入还没到过期时间，后面获取再没有刷新缓存之前都还是旧值
        DATA_MAP.put("1", "第一个对象被更改了!");
        Thread.sleep(3000);


        logTimeGet(LAZY_LOADING_CACHE, "1");
        Thread.sleep(1000);

        logTimeGet(LAZY_LOADING_CACHE, "1");

        logTimeGet(LAZY_LOADING_CACHE, "1");

        logTimeGet(LAZY_LOADING_CACHE, "1");


        System.out.println("由于是懒加载的所以得先获取一次2的值: " + loadIfPresent(LAZY_LOADING_CACHE, "2"));
        // 这里虽然修改了存储的值，但是由于缓存写入还没到过期时间，后面获取再没有刷新缓存之前都还是旧值
        DATA_MAP.put("2", "第二个对象被更改了!");
        // 故意睡眠大于缓存刷新时间， 修改了原值， 缓存应该会重新刷新新值，但是我们由于对这个关键字进行了不更新缓存操作，所以还是获取的最初的值
        Thread.sleep(3000);
        System.out.println("2的值被改变后我们在刷新中配置了某些key不需要刷新缓存，所以即使缓存刷了也不会更新");
        logTimeGet(LAZY_LOADING_CACHE, "2");

        System.out.println("=========================================");
        System.out.println(LAZY_LOADING_CACHE.stats());
    }


    /**
     * 初始化加载所有缓存， 缓存过期刷新的时候reload也会重新加载所有缓存而不是某一个key。
     * 相对于懒加载而言，这种方式，缓存一开始就被放到池中了，所以第一次获取也是从缓存中拿，
     * 但是会在启动时速度相对拖慢。
     * 当然这里也不是说一定的就是从缓存中拿就一定会快，还要看reload方法缓存过期的时候采用的是异步加载还是同步加载的
     */
    private static void initLoad() {
        System.out.println("==============================================initLoad===========================================");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        logTimeGet(INIT__LOADING_CACHE, "1");
        System.out.println("==============================================initLoad===========================================");
    }


    private static void logTimeGet(LoadingCache<String, String> loadingCache, String key) {
        long before = System.currentTimeMillis();
        System.out.println(loadIfPresent(loadingCache, key));
        long after = System.currentTimeMillis();
        System.out.println("耗时: " + (after - before));
        System.out.println("--------------------------------------");
    }

}
