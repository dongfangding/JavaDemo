package jdk.designPatterns.observer.impl;

import jdk.designPatterns.observer.Observer;
import jdk.designPatterns.observer.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 1. 添加观察者，addObserver
 * 2. 在被观察 字段的set方法中，需要即时通知观察者，调用noticeObserver
 * 3. noticeObserver方法体内获取当前观察者，调用观察者的update方法
 *
 * @author Administrator
 */
public class Lixi implements Subject {
    private double lixi; // 利息
    private Vector<Observer> observerVector; // 本金
    private List<Observer> observerList; // 本金

    public Lixi() {
        observerVector = new Vector<Observer>();
        observerList = new ArrayList<Observer>();
    }

    @Override
    public void addObserver(Observer observer) {
        observerVector.add(observer);
        observerList.add(observer);
    }

    @Override
    public void deleteObserver(Observer observer) {
        observerVector.remove(observer);
        observerList.remove(observer);
    }

    @Override
    public void noticeObserver() {
        for (Observer obv : observerVector) {
            obv.update();
        }
        for (Observer obl : observerList) {
            obl.update();
        }
    }

    public double getLixi() {
        return lixi;
    }

    public void setLixi(double lixi) {
        this.lixi = lixi;
        this.noticeObserver();
    }
}
