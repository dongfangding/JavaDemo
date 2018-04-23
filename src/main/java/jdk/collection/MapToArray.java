package jdk.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapToArray {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            list.add(new Random().nextInt());
        }

        Integer arr[] = list.toArray(new Integer[list.size()]);
        for (Integer it : arr) {
            System.out.println(it);
        }
    }

}
