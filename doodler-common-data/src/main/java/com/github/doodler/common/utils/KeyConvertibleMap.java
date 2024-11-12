package com.github.doodler.common.utils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description: KeyConvertibleMap
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public abstract class KeyConvertibleMap<K, V> extends AbstractMap<K, V>
        implements Map<K, V>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<K, V> delegate;
    private final Map<Object, K> keys;

    protected KeyConvertibleMap(Map<K, V> delegate) {
        this.delegate = delegate;
        this.keys = Collections.synchronizedMap(new HashMap<>());

        if (delegate.size() > 0) {
            delegate.entrySet().forEach(e -> {
                K key = e.getKey();
                keys.put(convertKey(key), key);
            });
        }
    }

    @Override
    public boolean containsKey(Object key) {
        Object realKey = keys.get(convertKey(key));
        return realKey != null && delegate.containsKey(realKey);
    }

    @Override
    public V get(Object key) {
        Object realKey = keys.get(convertKey(key));
        return realKey != null ? delegate.get(realKey) : null;
    }

    @Override
    public V put(K key, V value) {
        keys.put(convertKey(key), key);
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        Object realKey = keys.remove(convertKey(key));
        return realKey != null ? delegate.remove(realKey) : null;
    }

    @Override
    public void clear() {
        keys.clear();
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    protected abstract Object convertKey(Object key);
}
