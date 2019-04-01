package jdk.interview;

import java.util.Arrays;
import java.util.List;

/**
 * @author DDf on 2019/4/1
 */
public class ArraysAsListAdd {


    /**
     *
     这里使用【list.add】可能会导致UnsupportedOperationException less... (Ctrl+F1)
     Inspection info:
     使用工具类Arrays.asList()把数组转换成集合时，不能使用其修改集合相关的方法，它的add/remove/clear方法会抛出UnsupportedOperationException异常。

     Positive example:
     List<String> t   = Arrays.asList("a","b","c");
     //warn
     t.add("22");
     //warn
     t.remove("22");
     //warn
     t.clear();
     * @param args
     */
    public static void main(String[] args) {
        String[] arr = {"1", "2", "3"};
        List<String> list = Arrays.asList(arr);
        list.add("4");
        list.forEach(System.out::println);
    }
}
