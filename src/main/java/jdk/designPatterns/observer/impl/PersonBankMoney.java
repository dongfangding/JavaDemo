package main.java.jdk.designPatterns.observer.impl;

import main.java.jdk.designPatterns.observer.Observer;

public class PersonBankMoney implements Observer {
	private String name;
	private Double money;
	private Double totalMoney;
	private Lixi lixi;
	
	public PersonBankMoney(String name, Double money, Lixi lixi){
		this.name = name;
		this.money = money;
		this.lixi = lixi;
	}
	@Override
	public void update() {
		this.totalMoney = lixi.getLixi() * money;
	}
	@Override
	public String toString() {
		return("姓名:" + name + "\t本金：" + money + "\t当前利息:" + 
					lixi.getLixi() + "\t最后金额:" + totalMoney);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}
}
