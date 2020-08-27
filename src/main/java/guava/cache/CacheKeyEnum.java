package guava.cache;

/**
 * <p>缓存key</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/08/13 11:41
 */
public enum CacheKeyEnum {
    /**
     * 演示懒加载的缓存
     * {0} id
     * @see LoadingCacheDemo1
     */
    LOADING_CACHE_DEMO1_LAZY("LoadingCacheDemo1:LAZY:{0}"),

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
