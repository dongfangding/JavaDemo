package leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>https://leetcode-cn.com/problems/two-sum/</p >
 *
 *
 * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/two-sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/10/13 10:08
 */
public class TwoSum {


    public static void main(String[] args) {
        int[] nums = {2, 7, 1, 11, 8, 15, 8};
        int target = 9;
        List<String> position = twoSum(nums, target);

        for (String pos : position) {
            System.out.println(pos);
        }

        int[] official  = twoSum2(nums, target);
        for (int i : official) {
            System.out.println(i);
        }
    }

    /**
     * 可以找到多个满足条件的版本
     * @param nums
     * @param target
     * @return
     */
    public static List<String> twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>(nums.length);
        // 满足条件的角标对应条件
        List<String> position = new ArrayList<>(nums.length);
        for (int i = 0; i < nums.length; i ++) {
            if (!map.containsKey(target - nums[i])) {
                map.put(nums[i], i);
                continue;
            }
            position.add(i + "," + map.get(target - nums[i]));
            // 防止一个元素多次匹配
            map.remove(nums[i]);
            // 防止一个元素多次匹配
            map.remove(target - nums[i]);
        }
        return position;
    }


    public static int[] twoSum2(int[] nums, int target) {
        Map<Integer, Integer> hashtable = new HashMap<>();
        for (int i = 0; i < nums.length; ++i) {
            if (hashtable.containsKey(target - nums[i])) {
                return new int[]{hashtable.get(target - nums[i]), i};
            }
            hashtable.put(nums[i], i);
        }
        return new int[0];
    }

}
