package main.java.jdk.designPatterns.statue;

public class RedColor implements Color {
	private Light light;
	RedColor(Light light) {
		this.light = light;
	}
	@Override
	public void show() {
		System.out.println("红灯亮了全部停车！");
		light.setColor(new GreenColor(light));
	}

}
