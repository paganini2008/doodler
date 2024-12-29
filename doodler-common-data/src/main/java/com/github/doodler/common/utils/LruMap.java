package com.github.doodler.common.utils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

/**
 * @Description: LruMap
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public class LruMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = -1958272189245104075L;

    public LruMap(int maxSize) {
        this(maxSize, null);
    }

    public LruMap(Map<K, V> delegate, int maxSize) {
        this(delegate, maxSize, null);
    }

    public LruMap(int maxSize, RemovalListener<V> removalListener) {
        this(new ConcurrentHashMap<>(), maxSize, removalListener);
    }

    public LruMap(final Map<K, V> delegate, final int maxSize,
            final RemovalListener<V> removalListener) {
        this.delegate = delegate;
        this.keys =
                new ConcurrentLinkedHashMap.Builder<K, Object>().maximumWeightedCapacity(maxSize)
                        .weigher(Weighers.singleton()).listener((eldestKey, v) -> {
                            V eldestValue = delegate.remove(eldestKey);
                            if (removalListener != null) {
                                removalListener.onRemoval(eldestKey, eldestValue);
                            }
                        }).build();
    }

    private final Map<K, V> delegate;
    private final Map<K, Object> keys;

    @Override
    public V get(Object key) {
        keys.get(key);
        return delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        keys.put(key, key);
        return delegate.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        keys.putIfAbsent(key, key);
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public V remove(Object key) {
        keys.remove(key);
        return delegate.remove(key);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void clear() {
        keys.clear();
        delegate.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        keys.get(key);
        return delegate.containsKey(key);
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    public Map<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
