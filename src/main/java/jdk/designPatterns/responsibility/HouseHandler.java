package main.java.jdk.designPatterns.responsibility;

public class HouseHandler implements Handler{
	private Handler handler;
	@Override
	public void handlerRequest(Boy boy) {
		if(boy.isHasHouse()) {
			System.out.println("我有房子，程序不继续处理!");
		} else {
			System.out.println("我没有房子，程序继续处理，看你还有什么！");
			handler.handlerRequest(boy);
		}
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	

}
