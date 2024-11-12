package com.github.doodler.common.utils;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description: LruList
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
public class LruList<E> extends AbstractList<E> implements List<E>, Serializable {

    private static final long serialVersionUID = -6581751196667044003L;

    public LruList(int maxSize) {
        this(new CopyOnWriteArrayList<E>(), maxSize);
    }

    public LruList(List<E> delegate, int maxSize) {
        this(delegate, maxSize, null);
    }

    public LruList(int maxSize, RemovalListener<E> removalListener) {
        this(new CopyOnWriteArrayList<E>(), maxSize, removalListener);
    }

    public LruList(final List<E> delegate, final int maxSize, final RemovalListener<E> removalListener) {
        this.delegate = delegate;
        this.keys = new ConcurrentLinkedHashMap.Builder<Integer, E>().maximumWeightedCapacity(maxSize).weigher(
                        Weighers.singleton())
                .listener((index, eldestValue) -> {
                    delegate.remove(index);
                    if (removalListener != null) {
                        removalListener.onRemoval(index, eldestValue);
                    }
                }).build();
    }

    private final List<E> delegate;
    private final Map<Integer, E> keys;
    private volatile int index = 0;

    @Override
    public boolean add(E e) {
        synchronized (keys) {
            keys.put(index++, e);
            return delegate.add(e);
        }
    }

    @Override
    public boolean contains(Object o) {
        if (delegate.contains(o)) {
            keys.get(delegate.indexOf(o));
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        delegate.clear();
        keys.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public boolean remove(Object o) {
        if (delegate.remove(o)) {
            keys.remove(delegate.indexOf(o));
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public E get(int index) {
        E e = delegate.get(index);
        if (e != null) {
            keys.get(index);
        }
        return e;
    }

    @Override
    public void add(int index, E e) {
        delegate.add(index, e);
        keys.put(index, e);
    }

    @Override
    public E set(int index, E e) {
        E previous = delegate.set(index, e);
        keys.put(index, e);
        return previous;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    public List<E> getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}