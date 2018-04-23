package jdk.designPatterns.statue;

public class GreenColor implements Color {
    private Light light;

    GreenColor(Light light) {
        this.light = light;
    }

    @Override
    public void show() {
        System.out.println("当前是绿灯，可以行驶！");
        light.setColor(new YellowColor(light));
    }

}
