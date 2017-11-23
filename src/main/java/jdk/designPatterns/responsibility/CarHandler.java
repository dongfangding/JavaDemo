package main.java.jdk.designPatterns.responsibility;

public class CarHandler implements Handler {
	private Handler handler;
	@Override
	public void handlerRequest(Boy boy) {
		if(boy.isHasCar()) {
			System.out.println("我有车，程序被处理！");
		} else {
			System.out.println("我没有车，继续处理！");
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
