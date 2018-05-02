package json.jackson;

/**
 * Created by DDf on 2018/4/27
 */
public class Person {
    private String name;
    private String age;
    private String time;

    public Person(String name, String age, String time) {
        this.name = name;
        this.age = age;
        this.time = time;
    }

    public Person() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
