package jdk.other;

import java.util.ArrayList;
import java.util.List;

public class NewObjectIsFuGai {

    public static void main(String[] args) {
        List<Student> stuList = new ArrayList<Student>();
        List<Student> stuListNew = new ArrayList<Student>();
        Student stu1 = new Student();
        stu1.setName("name1");
        stu1.setAge("21");
        Student stu2 = new Student();
        stu2.setName("name2");
        stu2.setAge("22");
        Student stu3 = new Student();
        stu3.setName("name3");
        stu3.setAge("23");
        stuList.add(stu1);
        stuList.add(stu2);
        stuList.add(stu3);
        Student stu = new Student();
        for (Student s : stuList) {
            stu.setName(s.getName());
            stu.setAge(s.getAge());
            stuListNew.add(stu);
            //System.out.println("stu:" + s.getName() + "\tage:" + s.getAge() );
        }


        for (Student s1 : stuList) {
            System.out.println(s1.getName() + "\t" + s1.getAge());
        }
    }

}
