package main.java.jdk.designPatterns.iterator.impl;

import java.util.List;

import main.java.jdk.designPatterns.iterator.Iterator;
import main.java.jdk.designPatterns.iterator.IteratorController;

public class IteratorControllerImpl<T> implements IteratorController<T> {
	private List<T> list;
	public IteratorControllerImpl(List<T> list){
		this.list = list;
	}
	@Override
	public Iterator<T> createIterator() {
		return new IteratorImpl<T>(list);
	}
}
