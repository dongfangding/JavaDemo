package jdk.designPatterns.flyWeight;

import org.junit.Test;

/**
 * 享元模式
 * 个人理解： 工厂类不应该被允许多次实例化，所以用private修饰工厂类构造方法
 * 在工厂类中实例化对象时，被实例化的对象允许通过构造方法来实例化对象，那么如果屏蔽在客户端不允许跳过工厂直接实例化对象。
 * 问题在于，如果用private修饰对象的构造方法，那么在工厂类中也无法实例化对象。所以如何两全其美？
 *
 * @author DDf on 2016年8月11日下午5:03:25
 */
public class TestFlyWeight {

    @Test
    public void test() {
        System.out.println("连接池内的连接数量为：" + CarFlyWeightFactory.getCarPoolNum());
        Car car = null;
        try {
            car = CarFlyWeightFactory.getCar(FordCar.class);
            car.showCarName();
            Car car1 = CarFlyWeightFactory.getCar(FordCar.class);
            car1.showCarName();
            Car car2 = CarFlyWeightFactory.getCar(main.java.jdk.designPatterns.flyWeight.TruckCar.class);
            car2.showCarName();
            System.out.println("car == car1:" + (car.equals(car1)));
            System.out.println("连接池内的连接数量为：" + CarFlyWeightFactory.getCarPoolNum());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
