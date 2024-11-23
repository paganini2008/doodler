package com.github.doodler.common.utils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @Description: MutableMap
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public abstract class MutableMap<K, V> extends AbstractMap<K, V>
        implements Map<K, V>, Serializable {

    private static final long serialVersionUID = 1L;

    protected final Map<K, V> delegate;

    protected MutableMap(Map<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V get(Object key) {
        return delegate.get(mutate(key));
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(mutate(key), value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(mutate(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(mutate(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
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
    public int size() {
        return delegate.size();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    protected abstract K mutate(Object key);
}
