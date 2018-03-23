/**
 * 
 */
package main.java.jdk.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * @author DDF 2018年3月21日
 *
 */
public class StreamDemo {
	
	@Test
	public void test() {
		// 使用Arrays的静态方法获取一个String类型的流
		String[] arr = new String[] {"1", "2", "3"};
		Stream arrStream = Arrays.stream(arr);
		System.out.println(arrStream.findFirst());
		
		
		// 使用Arrays的静态方法获取一个String类型的流
		int[] intArr = new int[] {1, 2, 3};
		IntStream intStream = Arrays.stream(intArr);
		System.out.println(intStream.findFirst());
		
		
		List<String> list1 = new ArrayList<>();
		for (int i = 0; i < 10; i ++) {
			list1.add("元素" + i);
		}
		Stream stream1 = list1.stream();
		System.out.println(stream1.findFirst());
	}
	
	@Test
	public void test1() {
		List<Person> personList = new ArrayList<>();
		personList.add(new Person(1, "张三", "安徽"));
		personList.add(new Person(2, "李四", "上海"));
		personList.add(new Person(3, "王二", "福建"));
		personList.add(new Person(4, "麻子", "江苏"));
		personList.add(new Person(5, "小红", "安徽"));
		
		System.out.println("打印所有的数据");
		personList.stream().forEach(System.out::println);
		
		// 查找对象id为1的数据
		System.out.println("查找对象id为1的数据");
		Integer id = 1;
		personList.stream().
				filter(x -> x.getId() == id).
				collect(Collectors.toList()).
				forEach(System.out::println);
		
		// 查找所有安徽的数据
		System.out.println("查找所有安徽的数据");
		String city = "安徽";
		personList.stream().filter(x -> x.getCity().equals(city)).collect(Collectors.toList())
			.forEach(System.out::println);
		
		// 查找所有人员存在哪些区域，需要去重
		System.out.println("查找所有人员存在哪些区域，需要去重");
		personList.stream().map(Person::getCity).distinct().forEach(System.out::println);
		
		
		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map1 = new HashMap<>();
		map1.put("id", 1);
		map1.put("name", "张三");
		map1.put("city", "安徽");
		
		Map<String, Object> map2 = new HashMap<>();
		map2.put("id", 2);
		map2.put("name", "李四");
		map2.put("city", "上海");
		mapList.add(map1);
		mapList.add(map2);
		
		// 查找里面Map 的id为1的数据
		System.out.println("查找里面Map 的id为1的数据");
		Integer MapId = 1;
		mapList.stream().filter((map) -> map.get("id").equals(MapId)).collect(Collectors.toList())
			.forEach(System.out::println);
	}
	
	class Person {
		private Integer id;
		private String name;
		private String city;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		
		Person() {
			
		}
		
		Person(Integer id, String name, String city) {
			this.id = id;
			this.name = name;
			this.city = city;
		}
		@Override
		public String toString() {
			return "Person [id=" + id + ", name=" + name + ", city=" + city + "]";
		}
	}
}
