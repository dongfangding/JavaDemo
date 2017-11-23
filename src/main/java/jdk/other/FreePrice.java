package main.java.jdk.other;

public class FreePrice {
	/**
	 * 仅供参考，不知对否
	 * @param args
	 * @author Adminnistrator
	 */
	public static void main(String[] args) {
		double totalPrice = 0;
		int num = 50; //这个是买东西的总数量
		double price = 1; //你没说每个东西的单价，这个是单价
		
		// 这个是最基本的实现，小学数学就可以算的。本来应该付的钱减去免费的钱，就是最后应该付的钱。50/3是取整，不满三个不给优惠
		System.out.println("最后应该付钱：" + ((price * 50) - (price * (50 /3))));
		
		// 这个算法就是对3取余，也就是第三个不计入价格。每次买三个，只算两个的钱。
		for(int i = 1; i <= num; i ++){
			if((i % 3) != 0){
				totalPrice = totalPrice + price;
			}
		}
		System.out.println("总价totalPrice = " + totalPrice);
	}
}
