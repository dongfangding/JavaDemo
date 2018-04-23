package jdk.designPatterns.observer;

/**
 * 目标(Subject)接口:
 * 建立一个注册观察者对象的接口; public void addObserver(Observer o);
 * 建立一个删除观察者对象的接口; public void deleteObserver(Observer o);
 * 建立一个当目标状态发生改变时,发布通知给观察者对象的接口; public void noticeObserver();
 *
 * @author Administrator
 */
public interface Subject {
    // 添加观察者
    public void addObserver(Observer observer);

    // 删除观察者
    public void deleteObserver(Observer observer);

    // 通知观察者
    public void noticeObserver();

}
