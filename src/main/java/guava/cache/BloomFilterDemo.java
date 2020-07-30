package guava.cache;

import cn.hutool.core.util.RandomUtil;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * <p>description</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/07/30 19:09
 */
public class BloomFilterDemo {

    public static void main(String[] args) {
        checkRepeat();
    }


    /**
     * 判断重复元素
     */
    public static void checkRepeat() {
        int maxValue = Integer.MAX_VALUE >> 5;
        Set<Integer> setValue = new HashSet<>(maxValue);
        BloomFilter<Integer> integerBloomFilter = BloomFilter.create(Funnels.integerFunnel(), maxValue);
        int current;
        for (int i = 0; i < maxValue; i++) {
            current = RandomUtil.randomInt(0, maxValue);
            setValue.add(current);
            integerBloomFilter.put(current);
        }

        System.out.println("一共生成元素数量: " + maxValue);
        System.out.println("标准重复数量总和: " + (maxValue - setValue.size()));
        System.out.println("布隆过滤器判断的重复元素总和: " + (maxValue - integerBloomFilter.approximateElementCount()));

        new Scanner(System.in).nextLine();
    }
}
