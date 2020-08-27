package guava.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jdk.reflect.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>description</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/08/27 14:46
 */
@Slf4j
public class GuavaCacheCanNotDeleteKeyDemo {
    /**
     * 原始数据集合,模拟数据库记录
     */
    private final static Map<String, Person> DATA_MAP;

    /**
     * 初始化直接缓存全部资
     */
    private static LoadingCache<String, Person> INIT_LOADING_CACHE;

    /**
     * reload线程池
     */
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500), new ThreadFactoryBuilder().setNameFormat("reload-executor-pool").build());

    static {
        // 初始化原始数据
        DATA_MAP = new HashMap<>();
        DATA_MAP.put("1", new Person("1", "jack"));
        DATA_MAP.put("2", new Person("2", "tom"));

        // 初始化缓存
        initLazyCache();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==============================================initLoad===========================================");

        String id = "2";
        System.out.println("由于缓存进行了启动初始化加载，所以第一次如果key存在，会直接拿缓存");
        logTimeGet(INIT_LOADING_CACHE, CacheKeyEnum.LOADING_CACHE_DEMO1_INIT.getTemplate(), id);

        DATA_MAP.put(id, new Person(id, DATA_MAP.get(id).getUsername() + "被修改了"));
        System.out.println("id=4的对象，由于没有到刷新时间, 所以用的还是旧值");
        logTimeGet(INIT_LOADING_CACHE, CacheKeyEnum.LOADING_CACHE_DEMO1_INIT.getTemplate(), id);

        Thread.sleep(3000);
        System.out.println("故意睡眠超过过期时间，过期之后的第一次请求触发刷新操作，由于我们使用了闭锁，让获取值的线程强制等待刷新任务完整，所以这里会耗时比较久，但值是最新的");
        logTimeGet(INIT_LOADING_CACHE, CacheKeyEnum.LOADING_CACHE_DEMO1_INIT.getTemplate(), id);

        // 下面就要演示如果这个key被删除了，其实最终guava不接受为null的value,会导致这个无效key一直存在，且对应的value为最后一次缓存的数据
        DATA_MAP.remove(id);
        // 故意超过过期时间，保证会触发reload
        Thread.sleep(3000);
        System.out.println("模拟数据库删除key=4的数据");
        // 我们已经使用了闭锁来保证刷新时的同步获取来保证演示效果， 这里guava会抛出异常CacheLoader returned null for key LoadingCacheDemo1:INIT:4.
        // 因为它不接受为null的value, 最终这个缓存会无法置空, 然后就悲剧了，这个key的value我们以为是null，实际一直是最后一次缓存的数据
        logTimeGet(INIT_LOADING_CACHE, CacheKeyEnum.LOADING_CACHE_DEMO1_INIT.getTemplate(), id);
        System.out.println("打印一下缓存中的数据，需要说明的是，这里不是由于缓存未刷新造成的数据不一致，而是由于刷新时对应key的记录变成了null而guava不接受所造成的");
        System.out.println(INIT_LOADING_CACHE.asMap());

        EXECUTOR.shutdown();
    }

    /**
     * 模拟获取原始数据
     * @param key
     * @return
     */
    public static Person get(String key) {
        try {
            // 模拟向数据库中取数据需要耗时1000毫秒
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DATA_MAP.get(key);
    }

    private static <T> void logTimeGet(LoadingCache<String, T> loadingCache, String template, String... parameter) {
        long before = System.currentTimeMillis();
        System.out.println("缓存中[" + MessageFormat.format(template, parameter) + "]对应的值: "
                + LocalCacheUtil.getGuavaCache(loadingCache, template, parameter));
        long after = System.currentTimeMillis();
        System.out.println("耗时: " + (after - before));
        System.out.println("--------------------------------------");
    }


    /**
     * 初始化缓存
     */
    private static void initLazyCache() {
        INIT_LOADING_CACHE = CacheBuilder.newBuilder()
                .maximumSize(500)
                .recordStats()
                .refreshAfterWrite(3, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Person>() {
                    @Override
                    public Person load(String key) throws Exception {
                        System.out.println("=================load===================: " + key);
                        if (StringUtil.isBlank(key)) {
                            return null;
                        }
                        String[] data= key.split(CacheKeyEnum.SPLIT_CHAR);
                        // LoadingCacheDemo1:INIT:{0}
                        String id = data[2];
                        return get(id);
                    }

                    @Override
                    public ListenableFuture<Person> reload(String key, Person oldValue) throws Exception {
                        // 如果使用异步的话，短期内多次获取，因为此时异步尚未执行结束，大概率会造成获取的时候使用的还是旧的缓存值
                        ListenableFutureTask<Person> task = ListenableFutureTask.create(() -> load(key));
                        EXECUTOR.execute(task);
                        // 可选是否采用闭锁再刷新的时候让读线程等待刷新完成，一般不需要，就异步刷新即可
                        task.get();
                        return task;
                    }
                });
        // 这里也可以直接初始化缓存，就不用懒加载获取某个key的时候再去缓存
        DATA_MAP.forEach((k, v) -> {
            INIT_LOADING_CACHE.put(MessageFormat.format(CacheKeyEnum.LOADING_CACHE_DEMO1_INIT.getTemplate(), k), v);
        });
    }

    /**
     * 获取对应缓存中的值
     * @param loadingCache
     * @param template
     * @param parameter
     * @param <T>
     * @return
     */
    public static <T> T getGuavaCache(LoadingCache<String, T> loadingCache, String template, String... parameter) {
        try {
            return loadingCache.get(MessageFormat.format(template, parameter));
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (Exception e) {
            log.error("查询缓存异常 template={},parameter={}", template, parameter);
            return null;
        }
    }

    /**
     * 缓存key维护
     */
    public enum CacheKeyEnum {
        /**
         * 演示初始化加载的缓存
         * {0} id
         */
        LOADING_CACHE_DEMO1_INIT("LoadingCacheDemo1:INIT:{0}"),

        ;

        public static final String SPLIT_CHAR = ":";

        CacheKeyEnum(String template) {
            this.template = template;
        }

        private final String template;

        public String getTemplate() {
            return template;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person {

        private String id;

        private String username;
    }

}
