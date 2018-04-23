package jdk.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetDemo {
    public static void main(String args[]) {
        Long id = new Long(555);
        Long id1 = new Long(333);
        Long id2 = new Long(555);
        Set<Long> set = new HashSet<Long>();
        set.add(id);
        set.add(id1);
        set.add(id2);
        System.out.println(set.size());

        Map<Long, String> map = new HashMap<Long, String>();
        map.remove(null);
    }
}
