package jdk.designPatterns.flyWeight;

import java.util.HashMap;
import java.util.Map;

public class CarFlyWeightFactory {
    private CarFlyWeightFactory() {

    }

    private static Car car;
    private static Map<String, Car> carPool = new HashMap<String, Car>();

    public static Car getCar(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        if (clazz.getSimpleName().equals(FordCar.class.getSimpleName())) {
            car = carPool.get(clazz.getSimpleName());
            if (car == null) {
                car = (Car) clazz.newInstance();
                carPool.put(clazz.getSimpleName(), car);
            }
        } else if (clazz.getSimpleName().equals(TruckCar.class.getSimpleName())) {
            car = carPool.get(clazz.getSimpleName());
            if (car == null) {
                car = (Car) clazz.newInstance();
                carPool.put(clazz.getSimpleName(), car);
            }
        } else {
            throw new NullPointerException("获取实例化出错！");
        }
        return car;
    }

    public static int getCarPoolNum() {
        return carPool.size();
    }
}
