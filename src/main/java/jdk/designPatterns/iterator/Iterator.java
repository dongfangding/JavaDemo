package jdk.designPatterns.iterator;

public interface Iterator<T> {
    public T first();

    public T next();

    public T current();

    public boolean isOver();
}
