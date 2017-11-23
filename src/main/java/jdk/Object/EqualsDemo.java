package main.java.jdk.Object;


class EqualsDemo {
	private static Long EQUALS_NUM = new Long("100000000");
	
	public static void main(String arge[]) {
		Entity entity = new Entity(new Long("100000000"));
		System.out.println(EQUALS_NUM.equals(entity.getNum()));
	}
}



