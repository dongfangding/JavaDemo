package jdk.designPatterns.iterator;

public interface IteratorController<T> {
    public Iterator<T> createIterator();
}
