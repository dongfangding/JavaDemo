package main.java.jdk.junit;

public class Calculator {
	private int result = 0;
	private int num = 0;
	private int initNum = 0;
	
	Calculator() {
		
	}
	
	Calculator(int num, int initNum) {
		this.num = num;
		this.initNum = initNum;
	}
	
	public void add(int num) {
		result += num;
	}
	
	public void substract(int num) {
		result -= num;
	}
	
	public void square(int num) {
		result *= num;
	}
	
	public void devide(int num) {
		result /= num;
	}
	
	public void clear() {
		result = 0;
	}
	
	public int getResult() {
		return result;
	}
	
	public void igore() {
		
	}
	
	public void siXunHian() {
		for(;;) {
			System.out.println("死循环");
		}
	}
	
	@SuppressWarnings("null")
	public void nullPointException() {
		String nullStr = null;
		if(nullStr.equals("null")) {
			System.out.println("匹配");
		}
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getInitNum() {
		return initNum;
	}

	public void setInitNum(int initNum) {
		this.initNum = initNum;
	}
	
	
}
