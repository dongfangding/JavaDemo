package jdk.collection;

import java.util.WeakHashMap;

/**
 * @author DDf on 2019/4/15
 */
public class WeakHashMapDemo {

	public static void main(String[] args) {
		WeakHashMap<String, String> weakHashMap = new WeakHashMap<>();
		String key1 = new String("1");
		String key2 = new String("2");
		weakHashMap.put(key1, "第一个值");
		weakHashMap.put(key2, "第二个值");
		System.out.println(weakHashMap);
		System.gc();
		System.out.println(weakHashMap);
	}
}
