package main.java.jdk.Object;

public class ArgsAssign {

	public static void main(String[] args) {
		Test a = new Test(5, 6);
		a.test_m(a);
		a.toString();
		
		Test b = new Test(1, 2);
		b.test_2(b);
		b.toString();
		
		Test c = new Test(1, 2);
		c.test_3(c);
		c.toString();
		
	}
}

class Test{
	int i, j;
	public void test_m(Test t) {// t到底是对象，还是对象引用？
		Test b = new Test(1, 2); // 创建了新的对象引用b，并创建对象初始化数据1，2
		System.out.println(b);
		System.out.println(t);
		t = b; // 现在t和b都指向了新的b地址，指向了新的地址,原对象就和t和b没有任何关系了，所以不会改变原对象的值？
		System.out.println("--");
		System.out.println(b);
		System.out.println(t);
	}
	
	// 这个是不是可以理解为上面text_m方法的缩写？
	public void test_2(Test t) {
		t = new Test(5, 6); // t指向了新的对象引用地址，所以改变后，和原来的对象没什么关系，所以不变
	}
	
	public void test_3(Test t) {
		Test b = t; // 把t的对象引用地址赋给了b,所以b和t指向了同一个对象，都会对原对象的数据产生影响
		b.i = 5;
		b.j = 6;
	}
	
	public Test() {
		
	}
	public Test(int i , int j) {
		this.i = i;
		this.j = j;
	}
}
