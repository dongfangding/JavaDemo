package jdk.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConcurrentHashMapTest {
    private static Map<String, Object[]> onlineMap = new ConcurrentHashMap<String, Object[]>();

    public static void main(String[] args) {
        onlineMap.put("1", new Object[]{"test"});
        onlineMap.remove(null);
        System.out.println("null ?");
    }

}
