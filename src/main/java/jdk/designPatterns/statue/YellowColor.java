package main.java.jdk.designPatterns.statue;

public class YellowColor implements Color{
	private Light light;
	YellowColor(Light light) {
		if(light == null)
			throw new NullPointerException();
		this.light = light;
	}
	@Override
	public void show() {
		System.out.println("黄灯亮了等一等！");
		light.setColor(new RedColor(light));
	}

}
