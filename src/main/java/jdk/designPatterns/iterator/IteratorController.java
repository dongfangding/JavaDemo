package main.java.jdk.designPatterns.iterator;

public interface IteratorController<T> {
	public Iterator<T> createIterator();
}
