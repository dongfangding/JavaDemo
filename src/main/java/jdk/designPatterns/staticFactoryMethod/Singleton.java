package main.java.jdk.designPatterns.staticFactoryMethod;

/**
 * 单例模式
 * (1)
 * 在设计模式中,Factory Method也是比较简单的一个,但应用非常广泛,EJB,RMI,COM,CORBA,Swing中都可以看到此模式
 * 的影子,它是最重要的模式之一.在很多地方我们都会看到xxxFactory这样命名的类.
 * (2)
 *	基本概念：
 * FactoryMethod是一种创建性模式,它定义了一个创建对象的接口,但是却让子类来决定具体实例化哪一个类.
 * 通常我们将Factory Method作为一种标准的创建对象的方法。
 * 应用方面：
 * 当一个类无法预料要创建哪种类的对象或是一个类需要由子类来指定创建的对象时我们就需要用到Factory Method 模
 * 式了.
-------------------------------- singelton(单例模式) --------------------------------*
 * 基本概念:
 * Singleton 是一种创建性模型,它用来确保只产生一个实例,并提供一个访问它的全局访问点.对一些类来说,保证只有一个实例是很重要的,比如有的时候,数据库连接或 Socket 连接要受到一定的限制,必须保持同一时间只能有一个连接的存在.
 * 运用：
 * 在于使用static变量；
 * 创建类对象，一般是在构造方法中，或用一个方法来创建类对象。在这里方法中，加对相应的判断即可。
 
 * 单态模式与共享模式的区别：
 * 单态模式与共享模式都是让类的实例是唯一的。
 * 但单态模式的实现方式是：
 * 在类的内部.即在构造方法中，或静态的getInstace方法中，进行判断，若实例存在，则直接返回，不进行创建；
 * 共享模式的实现方式是：
 * 每次要用到此实例时，先去此hashtable中获取，若获取为空，则生成实例，且将类的实例放在一人hashtable中，若获取不为空，则直接用此实例。
 * @author Administrator
 */
public class Singleton {
	public static Singleton s;
	public static String ins;
	public static Singleton getInstance() {
		ins = "1";
		if(s == null) {
			s = new Singleton();
		}
		ins = "2";
		System.out.println(ins);
		return s;
	}
	
	/**
	 * 私有构造方法，不允许外部new
	 */
	private Singleton (){
		
	}
}
