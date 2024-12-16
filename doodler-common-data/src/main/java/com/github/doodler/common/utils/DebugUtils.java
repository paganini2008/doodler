package com.github.doodler.common.utils;

import java.util.Iterator;
import java.util.Map;

import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: DebugUtils
 * @Author: Fred Feng
 * @Date: 01/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class DebugUtils {

    public <T> void info(Iterator<T> iterator) {
        for (; iterator.hasNext();) {
            System.out.println(iterator.next());
        }
    }

    public <T> void error(Iterator<T> iterator) {
        for (; iterator.hasNext();) {
            System.err.println(iterator.next());
        }
    }

    public <T> void info(Iterable<T> iterable) {
        for (T t : iterable) {
            System.out.println(t);
        }
    }

    public <T> void error(Iterable<T> iterable) {
        for (T t : iterable) {
            System.err.println(t);
        }
    }

    public <K, V> void info(Map<K, V> map) {
        for (Map.Entry<K, V> e : map.entrySet()) {
            System.out.println(e);
        }
    }

    public <K, V> void error(Map<K, V> map) {
        for (Map.Entry<K, V> e : map.entrySet()) {
            System.err.println(e);
        }
    }

    public <K, V> void info(Object[] array) {
        for (Object o : array) {
            System.out.println(o);
        }
    }

    public <K, V> void error(Object[] array) {
        for (Object o : array) {
            System.err.println(o);
        }
    }

}
