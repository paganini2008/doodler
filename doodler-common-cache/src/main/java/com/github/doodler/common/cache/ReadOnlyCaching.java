package com.github.doodler.common.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.cache.Cache;
import com.github.doodler.common.cache.filter.CacheMethodFilter;

/**
 * @Description: ReadOnlyCaching
 * @Author: Fred Feng
 * @Date: 26/09/2023
 * @Version 1.0.0
 */
public class ReadOnlyCaching implements InvocationHandler {

    private final String cacheName;
    private final Cache cache;
    private final CacheMethodFilter cacheMethodFilter;

    ReadOnlyCaching(String cacheName, Cache cache, CacheMethodFilter cacheMethodFilter) {
        this.cacheName = cacheName;
        this.cache = cache;
        this.cacheMethodFilter = cacheMethodFilter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String methodName = method.getName();
        switch (methodName) {
            case "equals":
                return false;
            case "hashCode":
                return System.identityHashCode(this);
            case "toString":
                return this.toString();
        }
        Object result = method.invoke(cache, args);
        if ("get".equals(methodName)) {
            cacheMethodFilter.onGet(cacheName, args[0], result);
        }
        return result;
    }

    public static Cache createProxy(String cacheName, Cache cacheObject,
                                    CacheMethodFilter cacheMethodFilter) {
        return (Cache) Proxy.newProxyInstance(cacheObject.getClass().getClassLoader(),
                new Class<?>[]{Cache.class},
                new ReadOnlyCaching(cacheName, cacheObject, cacheMethodFilter));
    }
}