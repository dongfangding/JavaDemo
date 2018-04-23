package jdk.collection;

import java.util.ArrayList;
import java.util.List;

public class ArrayListDemo {

    public static void main(String[] args) {
        ArrayListDemo.initArrayList();
    }

    public static void initArrayList() {
        List<String> list = new ArrayList<String>(20);
        System.out.println(list.size());
    }

}
