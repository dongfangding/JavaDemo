package jdk.designPatterns.iterator.impl;


import jdk.designPatterns.iterator.Iterator;

import java.util.ArrayList;
import java.util.List;

public class IteratorImpl<T> implements Iterator<T> {
    private int currentIndex = 0;
    List<T> list;

    public IteratorImpl(List<T> list) {
        this.list = new ArrayList<T>();
        this.list = list;
    }

    @Override
    public T first() {
        return list.get(0);
    }

    @Override
    public T next() {
        return list.get(currentIndex++);
    }

    @Override
    public T current() {
        return list.get(currentIndex);
    }

    @Override
    public boolean isOver() {
        return currentIndex > list.size() - 1;
    }
}
