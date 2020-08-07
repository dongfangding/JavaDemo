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
 * @date 2020/07/30 19:09AbstractQueuedSynchronizer
 */
public class BloomFilterDemo {

    public static void main(String[] args) throws InterruptedException {
        checkRepeat();
    }


    /**
     * 判断重复元素
     */
    public static void checkRepeat() throws InterruptedException {
        int maxValue = Integer.MAX_VALUE >> 10;
        Set<Integer> setValue = new HashSet<>(maxValue);
        BloomFilter<Integer> integerBloomFilter = BloomFilter.create(Funnels.integerFunnel(), maxValue);
        int current;
        for (int i = 0; i < maxValue; i++) {
            Thread.sleep(20);
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
