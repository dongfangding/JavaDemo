package jdk.designPatterns.observer;

/**
 * 观察者接口
 * 建立一个当收到目标通知后的更新接口: public void update();
 *
 * @author Administrator
 */
public interface Observer {
    // 根据被观察者数据的改变来更新数据
    public void update();
}
