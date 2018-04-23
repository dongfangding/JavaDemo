package jdk.collection;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListRemoveRep {
    public static void main(String[] args) {
        Student s1 = new Student("yichen", "23", "男", "121", "13162798272");
        Student s2 = new Student("ddf", "233", "男男", "131", "15665223333");
        Student s3 = new Student("yichen", "23", "男", "121", "13162798272");

        List<Student> stuList = new ArrayList<Student>();
        stuList.add(s1);
        stuList.add(s2);
        stuList.add(s3);
        // 前提是重写了对象的equals和hasCode方法
        Set<Student> stuSet = new HashSet<Student>();
        stuSet.addAll(stuList);
        System.out.println("姓名\t年龄\t性别\t学号\t电话");
        for (Student stu : stuSet) {
            System.out.print(stu.getName() + "\t");
            System.out.print(stu.getAge() + "\t");
            System.out.print(stu.getSex() + "\t");
            System.out.print(stu.getStuNo() + "\t");
            System.out.println(stu.getTelNo() + "\t");
        }
    }
}
