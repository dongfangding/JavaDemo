package jdk.datastracture;

import java.util.*;

/**
 * <p>description</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/07/27 13:37
 */
public class BitSetDemo {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 3, 4, 5, 6, 5, 10, 11, 12, 13, 10};
        // 去重
        simpleCheckRepeat(arr);

        // 登录日志
        logUserLogin();

    }


    /**
     * 去重
     * @param arr
     */
    public static void simpleCheckRepeat(int[] arr) {
        BitSet bitSet = new BitSet();
        List<Integer> repeatList = new ArrayList<>();
        for (int item : arr) {
            if (bitSet.get(item)) {
                repeatList.add(item);
            } else {
                bitSet.set(item);
            }
        }
        System.out.println("给定数组: " + Arrays.toString(arr));
        System.out.println("重复元素: " + repeatList);
    }


    /**
     * 一个字节=8位，8位可以存0~7这个值
     * 如果我有一堆用户，用户的id封顶最大为100000
     * 则BitSet中需要的最大位置是100000，
     * 100000 / 8 + 1 = 12501位
     * 12501 / 1024 = 12个字节即可存储
     *
     * 当然这只在数据量大的情况下效率才是比较明显的，
     * 数据少根本没必要
     *
     *
     */
    public static void logUserLogin() {
        // 默认64位
        // 64位为一个一个long类型的大小，初始化的数组长度为1就足够了
        BitSet firstBitSet = new BitSet(12501);

        // 用户数组
        int[] userIds = new int[] {0, 10, 50000, 99999};
        Random random = new Random();

        // 模拟第一天登录
        for (int userId : userIds) {
            firstBitSet.set(userId, random.nextBoolean());
        }

        // 模拟第二天登录
        BitSet secondBitSet = new BitSet(12501);
        for (int userId : userIds) {
            secondBitSet.set(userId, random.nextBoolean());
        }

        System.out.println("第一天用户登录情况");
        for (int userId : userIds) {
            System.out.println(userId + ": " + convertLoginFlag(firstBitSet.get(userId)));
        }

        System.out.println("====================================================================");

        System.out.println("第二天用户登录情况");
        for (int userId : userIds) {
            System.out.println(userId + ": " + convertLoginFlag(secondBitSet.get(userId)));
        }


        System.out.println("====================================================================");

        System.out.println("第一天和第二天都登录的用户");
        firstBitSet.and(secondBitSet);
        for (int userId : userIds) {
            if (firstBitSet.get(userId)) {
                System.out.println(userId);
            }
        }


        System.out.println("====================================================================");

        System.out.println("第一天和第二天都未登录的用户");
        // todo 如何计算连续两天都未登录的用户？
    }

    private static String convertLoginFlag(Boolean bool) {
        if (bool) {
            return "登录";
        }
        return "未登录";
    }
}


