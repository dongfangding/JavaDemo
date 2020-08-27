package jdk.juc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 设计一个最大访问次数的接口限制，如果没有达到最大访问次数，则本次访问成功，否则停止访问并返回null
 * 单机版
 * @author ddf
 * @since 2019-04-29
 */
public class AccessLimit {

	private Integer maxAccess;

	private volatile Integer maxRetries;

	private AtomicInteger accessTimes = new AtomicInteger(0);

	AccessLimit(Integer maxAccess, Integer maxRetries) {
		this.maxAccess = maxAccess;
		this.maxRetries = maxRetries;
	}
	AccessLimit() {
		new AccessLimit(3, 3);
	}

	AccessLimit(Integer maxRetries) {
		new AccessLimit(3, maxRetries);
	}

	private Map<String, AtomicInteger> accessMap = new ConcurrentHashMap<>();

	public Map<String, Integer> access() {
		String threadName = Thread.currentThread().getName();
		Integer currAccess = accessMap.get(threadName).get();
		if (currAccess <= maxAccess) {

		}
		return null;
	}
}
