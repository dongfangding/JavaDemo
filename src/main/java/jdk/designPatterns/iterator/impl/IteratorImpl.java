package main.java.jdk.designPatterns.iterator.impl;

import java.util.ArrayList;
import java.util.List;

import main.java.jdk.designPatterns.iterator.Iterator;
public class IteratorImpl<T> implements Iterator<T>{
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
