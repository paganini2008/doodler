package com.github.doodler.common.utils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.util.Assert;

/**
 * @Description: KeyMatchedMap
 * @Author: Fred Feng
 * @Date: 10/02/2023
 * @Version 1.0.0
 */
public abstract class KeyMatchedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> implements Map<K, V>,
		Serializable {

	private static final long serialVersionUID = 1L;
	private final Map<K, V> delegate;
	private final Set<K> keys;

	protected KeyMatchedMap(final Map<K, V> delegate, final boolean matchFirst) {
		this.delegate = delegate;
		keys = Collections.synchronizedNavigableSet(new TreeSet<K>(new Comparator<K>() {

			public int compare(K left, K right) {
				return matchFirst ? LangUtils.compareTo(left, right) : LangUtils.compareTo(right, left);
			}
		}));
	}

	@Override
	public boolean containsKey(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.containsKey(key) : false;
	}

	public boolean existsKey(Object inputKey) {
		Assert.notNull(inputKey, "Nullable key");
		return delegate.containsKey(inputKey);
	}

	@Override
	public V get(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.get(key) : null;
	}

	@Override
	public V put(K key, V value) {
		Assert.notNull(key, "Nullable key");
		keys.add(key);
		return delegate.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V v = null;
		if (!existsKey(key)) {
			v = put(key, value);
		}
		return v;
	}

	@Override
	public V remove(Object inputKey) {
		K key = matches(inputKey);
		return key != null ? delegate.remove(key) : null;
	}

	@Override
	public void clear() {
		keys.clear();
		delegate.clear();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	private K matches(Object inputKey) {
		for (K key : keys) {
			if (match(key, inputKey)) {
				return key;
			}
		}
		return null;
	}

	protected abstract boolean match(K key, Object inputKey);
}