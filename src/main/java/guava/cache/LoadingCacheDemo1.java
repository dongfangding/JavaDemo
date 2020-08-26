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
 *
 * refreshAfterWrite模式，在写入之后指定的时间后，缓存会过期，但数据依然存在；只是说变成了旧数据，而且这个刷新不是主动刷新，是当有某个key
 *                     触发查询的时候，发现过了过期时间，就会触发refresh方法。可以将refresh方法实现为异步的，这样取值的线程就不会阻塞了，但是用的刷新前的旧值，当然
 *                     理论上如果刷新后旧值也可能和最新值是一样的，所以就看自己对数据是否敏感了。load方法是不会在key不存在的时候触发的， 只有
 *
 *
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
     * 用特定字符来代表存储的key对应的value是无效的
     */
    public static final String INVALID_STRING_VALUE = "INVALID_VALUE";

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
        DATA_MAP.put("4", "第四个对象");

        initLazyCache();

        initInitCache();

    }


    public static void main(String[] args) throws InterruptedException {
        // 演示基本获取， 数据原始集合更新的情况下，缓存未及时更新会存在数据不一致的问题
//        lazyInit();


        // 初始化加载全部缓存，不要和上面那个演示一起执行，因为上面的代码有睡眠，代码是阻塞的，会把这个演示对时间的预演不成立
        initLoad();

        EXECUTOR.shutdown();
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
                        String s = get(key);
                        if (s == null && INIT__LOADING_CACHE.asMap().containsKey(key)) {
                            System.out.printf("只有refreshAfterWrite模式的时候需要: 数据库中对应[%s]记录不存在，存入一个默认值[%s]，代表无效\n", key, INVALID_STRING_VALUE);
                            s = INVALID_STRING_VALUE;
                        }
                        return s;
                    }

                    /**
                     * reload默认调用的就是load方法，所以load方法需要实现如何加载缓存（即从原始数据源中取对应key的值），重新加载缓存。
                     * 该方法会在refreshAfterWrite缓存过期的时候被调用, 但内部不是一直在刷新，而是当refreshAfterWrite条件满足
                     * 之后，原先存储的数据已经过期了，但数据还在，如果再次获取某个key的时候才会检查是否是已经到达过期时间，
                     * 然后满足条件再次调用reload，也就是必须依赖于查询操作。
                     * 那么针对失效后刷新的第一个查询，如果异步加载，必然会造成获取的是oldValue.
                     * 这个时候如果想要获取到最新值，reload的实现就需要同步或者使用闭锁，否则获取的大概率就是旧的值了。
                     * 也就是这个刷新不是主动的，而是要由某个key的查询来触发
                     *
                     * @param key
                     * @param oldValue
                     * @return
                     * @throws Exception
                     */
                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        System.out.println("reload=============================: " + key);
                        // 有些键不需要刷新，并且我们希望刷新是异步完成的, 如果不需要刷新直接返回旧值，如果需要刷新则调用load方法重新缓存
                        if (neverNeedsRefresh(key)) {
                            return Futures.immediateFuture(oldValue);
                        } else {
                            // 如果使用异步的话，短期内多次获取，因为此时异步尚未执行结束，大概率会造成获取的时候使用的还是旧的缓存值
                            ListenableFutureTask<String> task = ListenableFutureTask.create(() -> load(key));
                            EXECUTOR.execute(task);
                            // 可选使用闭锁来让获取缓存的线程强制等待缓存加载完成使用最新值, 需要看load方法的执行时间以及对缓存不一致的接受程度自行决定
                            // task.get();
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
                        System.out.println("====================================: " + key);
                        if (StringUtil.isBlank(key)) {
                            return null;
                        }
                        String s = get(key);
                        if (s == null && INIT__LOADING_CACHE.asMap().containsKey(key)) {
                            System.out.printf("只有refreshAfterWrite模式的时候需要: 数据库中对应[%s]记录不存在，存入一个默认值[%s]，代表无效\r\n", key, INVALID_STRING_VALUE);
                            s = INVALID_STRING_VALUE;
                        }
                        return s;
                    }

                    /**
                     * reload默认调用的就是load方法，所以load方法需要实现如何加载缓存（即从原始数据源中取对应key的值），重新加载缓存。
                     * 该方法会在refreshAfterWrite缓存过期的时候被调用, 但内部不是一直在刷新，而是当refreshAfterWrite条件满足
                     * 之后，原先存储的数据已经过期了，但数据还在，如果再次获取某个key的时候才会检查是否是已经到达过期时间，
                     * 然后满足条件再次调用reload，也就是必须依赖于查询操作。
                     * 那么针对失效后刷新的第一个查询，如果异步加载，必然会造成获取的是oldValue.
                     * 这个时候如果想要获取到最新值，reload的实现就需要同步或者使用闭锁，否则获取的大概率就是旧的值了。
                     * 也就是这个刷新不是主动的，而是要由某个key的查询来触发
                     *
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
                             task.get();
                            return task;
                        }
                    }
                });
        // 这里也可以直接初始化缓存，就不用懒加载获取某个key的时候再去缓存
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
        try {
            return loadingCache.get(key);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 采用懒加载的方式加载缓存，当某个key被访问的时候才开始第一次获取并缓存，
     * 所以再缓存后到未过期之间的这段时间，第一次获取时耗时最长，后面会直接从缓存中获取
     *
     * 以及可能存在的缓存不一致、异步加载、缓存重新刷新、特定值不更新缓存等特性
     * @throws InterruptedException
     */
    private static void lazyInit() throws InterruptedException {
        System.out.println("======================================lazy cache =======================================================");
        System.out.println("第一次获取由于是懒加载，所以第一次不走缓存，会比较耗时: ");
        logTimeGet(LAZY_LOADING_CACHE, "1");

        // 这里虽然修改了存储的值，但是由于缓存写入还没到过期时间，后面获取再没有刷新缓存之前都还是旧值
        System.out.println("这里虽然修改了存储的值，但是由于缓存写入还没到过期时间，后面获取再没有刷新缓存之前都还是旧值 ");
        DATA_MAP.put("1", "第一个对象被更改了!");
        logTimeGet(LAZY_LOADING_CACHE, "1");
        Thread.sleep(1000);

        // 依然未到刷新时间，还是旧制
        System.out.println("依然未到刷新时间，还是旧制");
        logTimeGet(LAZY_LOADING_CACHE, "1");
        Thread.sleep(3000);

        // 到刷新时间了，但是刷新时发生在过期之后的第一次请求，这里是第一次请求，由于刷新被写成了是异步的（可以同步），所以大概率用的还是旧值
        System.out.println("到刷新时间了，但是刷新时发生在过期之后的第一次请求，这里是第一次请求，由于刷新被写成了是异步的（可以同步），所以大概率用的还是旧值");
        logTimeGet(LAZY_LOADING_CACHE, "1");

        // 刷新后的第二个请求，如果第一个请求
        System.out.println("刷新后的第二个请求，如果第一个异步刷新已经完成，则会获取到最新值，但是我们刷新获取最新值睡眠了一秒钟，所以这个请求用的还是旧制");
        logTimeGet(LAZY_LOADING_CACHE, "1");


        Thread.sleep(1000);
        // 这个睡眠的时间要大于异步刷新的时间
        System.out.println("睡眠1秒后等待刷新完成后，就可以获取到最新值了");
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
        System.out.println("======================================lazy cache =======================================================");
        System.out.println();
    }


    /**
     * 初始化加载所有缓存， 缓存过期刷新的时候reload也会重新加载所有缓存而不是某一个key。
     * 相对于懒加载而言，这种方式，缓存一开始就被放到池中了，所以第一次获取也是从缓存中拿，
     * 但是会在启动时速度相对拖慢。
     * 当然这里也不是说一定的就是从缓存中拿就一定会快，还要看reload方法缓存过期的时候采用的是异步加载还是同步加载的
     */
    private static void initLoad() throws InterruptedException {
        System.out.println("==============================================initLoad===========================================");
        System.out.println("由于缓存进行了启动初始化加载，所以第一次如果key存在，会直接拿缓存");
        logTimeGet(INIT__LOADING_CACHE, "4");

        DATA_MAP.put("4", "第四个对象被修改了");
        System.out.println("修改Key=4的对象，由于没有到刷新时间, 所以用的还是旧值");
        logTimeGet(INIT__LOADING_CACHE, "4");

        Thread.sleep(3000);
        System.out.println("故意睡眠超过过期时间，过期之后的第一次请求触发刷新操作，由于我们使用了闭锁，让获取值的线程强制等待刷新任务完整，所以这里会耗时比较久，但值是最新的");
        logTimeGet(INIT__LOADING_CACHE, "4");

        System.out.println("refreshAfterWrite模式不支持放入value为null， 但是如果之前这个key是存在的，在触发refresh方法的时候去查询发现数据不再了，return null的时候是无法将value置为null的，" +
                "所以那些取缓存用null判断都会有问题，这个情况下缓存的值永远都是最后一次的旧值。如果想删除，在外部是可以删除的，但是" +
                "一般而言，我们的刷新代码都是从数据库中去查询，在内部操作cache是无法删除的，而且在内部load的时候遍历cache确实是没有这个key的" +
                "，但是等待刷新动作完成后，如果return 的是null, 旧值又被填充回去了");

        System.out.println("对key主动调用invalidate,就可以将key清掉了，业务代码就可以通过null去判断。但现实往往没有那么理想，" +
                "由于刷新的动作是去数据库中做查询，我们直接在load方法中调用这个方法最终是无法删除的，也许当时删除了，但是当load方法走到最后return null的时候" +
                "旧值依然会被填充回缓存，这个key依然还是存在的。如果要解决这个问题，只能如果是String,我们用一个特定的字符代表这个key是不存在了，然后在外面使用的" +
                "时候多一层判断是否是默认值，然后再做删除。如果是对象，我们要放入默认对象代表这个缓存不存在，同样也要多判断一次是否是默认对象，然后在使用的地方来删除这个key");
        System.out.println();

        DATA_MAP.put("4", "第四个对象重新放回来");
        Thread.sleep(3000);
        System.out.println("将第四个对象再放回来");
        logTimeGet(INIT__LOADING_CACHE, "4");

        System.out.println("--------------------------");

        Thread.sleep(3000);
        // 这一步实际开发中不需要写，模拟的是数据库中对应key的值不存在了，这里置空后面重新加载才能发现值不在了
        System.out.println("模拟数据库中对应4的记录被删除-------");
        DATA_MAP.remove("4");
        Thread.sleep(3000);
        String s = loadIfPresent(INIT__LOADING_CACHE, "4");
        System.out.println("判断无效值之前取到的值为: " + s);
        // 使用refreshAtWrite模式一定要有默认值的处理，原因在上面说了
        if (INVALID_STRING_VALUE.equals(s)) {
            System.out.println("取出代表无效key的value，这个条件满足就不要执行业务了....然后调用invalidate方法区失效key,下个再去查询就为null了");
            INIT__LOADING_CACHE.invalidate("4");
        }
        System.out.println("调用一下invalidate之后看下缓存中的值，不能直接调用get, 因为get又会把默认值存进去");
        System.out.println(INIT__LOADING_CACHE.asMap());
        System.out.println("--------------------------");

        Thread.sleep(3000);
        logTimeGet(INIT__LOADING_CACHE, "5");


        System.out.println("我们加了如果值不存在就放入默认值也会带来另外一个问题，如果这个key本身在缓存中不存在，数据库中也查询不到，我们return null" +
                "的时候原先guava会报错，不会把key 和value存进去，但是我们放入默认值之后就会导致多维护一对垃圾键值对。所以需要在load方法里取原始数据" +
                "的时候多做一次判断，如果值为null并且这个key之前存在过，才放入默认值，否则剩下的就交给guava帮我处理就好了");

        Thread.sleep(3000);
        System.out.println("删除之后，本来调用get还会存入默认值；但是因为我们上面将key删除了，又加了上面一行的描述，所以这个时候垃圾键值对就不会被缓存了");
        logTimeGet(INIT__LOADING_CACHE, "4");
        System.out.println(INIT__LOADING_CACHE.asMap());

        System.out.println("==============================================initLoad===========================================");

    }


    private static void logTimeGet(LoadingCache<String, String> loadingCache, String key) {
        long before = System.currentTimeMillis();
        System.out.println("缓存中[" + key + "]对应的值: " + loadIfPresent(loadingCache, key));
        long after = System.currentTimeMillis();
        System.out.println("耗时: " + (after - before));
        System.out.println("--------------------------------------");
    }

}
