package main.java.entity;

public class Ostrich extends Bird{
	
	@Override
	public void fly(){
		System.out.println("我在地上蹦蹦跳跳的暴走。。");
	}
	
	public String toString(){
		return "一只" + super.getColor() + "的" + this.getAge() + "岁左右的" + this.getSex() + "鸵鸟在暴走。";
	}
}
