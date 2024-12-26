package com.github.doodler.common.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description: BoundedList
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
public class BoundedList<E> extends AbstractList<E> implements Serializable {

    private static final long serialVersionUID = 569726435668659831L;

    private final List<E> delegate;
    private final int maxSize;
    private final Queue<E> keys;
    private final RemovalListener<E> removalListener;

    @FunctionalInterface
    public static interface RemovalListener<E> {

        void onRemoval(E elderValue);
    }

    public BoundedList(int maxSize) {
        this(maxSize, null);
    }

    public BoundedList(int maxSize, RemovalListener<E> removalListener) {
        this(new CopyOnWriteArrayList<E>(), maxSize, removalListener);
    }

    public BoundedList(List<E> delegate, int maxSize, RemovalListener<E> removalListener) {
        this.delegate = delegate;
        this.maxSize = maxSize;
        this.keys = new ConcurrentLinkedQueue<E>();
        this.removalListener = removalListener;
    }

    @Override
    public boolean add(E e) {
        boolean result = delegate.add(e);
        ensureCapacity(e);
        return result;
    }

    @Override
    public E set(int index, E element) {
        E previous = delegate.set(index, element);
        if (previous != null) {
            keys.remove(previous);
        }
        ensureCapacity(element);
        return previous;
    }

    @Override
    public void add(int index, E element) {
        delegate.add(index, element);
        ensureCapacity(element);
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        if (delegate.remove(o)) {
            keys.remove(o);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (delegate.removeAll(c)) {
            keys.removeAll(c);
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (delegate.retainAll(c)) {
            keys.retainAll(c);
            return true;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E previous = delegate.remove(index);
        keys.remove(previous);
        return previous;
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    public Collection<E> getDelegate() {
        return delegate;
    }

    private void ensureCapacity(E e) {
        boolean reached;
        E eldestElement = null;
        synchronized (keys) {
            keys.add(e);
            if (reached = (keys.size() > maxSize)) {
                eldestElement = keys.poll();
                delegate.remove(eldestElement);
            }
        }
        if (reached && removalListener != null) {
            removalListener.onRemoval(eldestElement);
        }
    }
}
