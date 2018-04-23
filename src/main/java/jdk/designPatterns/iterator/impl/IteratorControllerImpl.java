package jdk.designPatterns.iterator.impl;

import jdk.designPatterns.iterator.Iterator;
import jdk.designPatterns.iterator.IteratorController;

import java.util.List;

public class IteratorControllerImpl<T> implements IteratorController<T> {
    private List<T> list;

    public IteratorControllerImpl(List<T> list) {
        this.list = list;
    }

    @Override
    public Iterator<T> createIterator() {
        return new IteratorImpl<T>(list);
    }
}
