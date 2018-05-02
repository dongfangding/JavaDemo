package json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by DDf on 2018/4/27
 * jackson的简单使用， 参见https://www.yiibai.com/jackson/
 */
public class ObjectMapperTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String str = "{\"name\": \"ddf\", \"age\": \"25\", \"time\": \"2018-04-27\"}";



    @Test
    public void testReadToMap() throws Exception {
        Map<String, Object> map = objectMapper.readValue(str, Map.class);
        map.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    public void testReadToPOJO() throws Exception {
        // 必须要有无参的构造函数
        Person person = objectMapper.readValue(str, Person.class);
        System.out.println(person.toString());
    }

    @Test
    public void testPOJOToJSON() throws IOException {
        Person person = new Person("ddf", "25", "2018-04-27");
        System.out.println(objectMapper.writeValueAsString(person));
        objectMapper.writeValue(new File("Person.json"), person);

        Map<String, Object> map = objectMapper.readValue(new File("Person.json"), Map.class);
        map.forEach((k, v) -> System.out.println(k + ":" + v));

        // 必须要有无参的构造函数
        Person person1 = objectMapper.readValue(new File("Person.json"), Person.class);
        System.out.println(person1.toString());
    }
}
