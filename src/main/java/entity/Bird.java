package main.java.entity;

public class Bird {
	private String sex;
	private Integer age;
	private String color;
	
	public void fly(){
		System.out.println("我在天空里自由自在地飞翔...");
	}

	@Override
	public String toString() {
		return "一只" + color + "的，大概" + age + "岁左右的" + sex + "鸟在天空里自由自在地飞翔...";
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
