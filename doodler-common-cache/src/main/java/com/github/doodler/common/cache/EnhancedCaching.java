package com.github.doodler.common.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.cache.Cache;
import com.github.doodler.common.cache.filter.CacheMethodFilter;

/**
 * @Description: EnhancedCaching
 * @Author: Fred Feng
 * @Date: 31/01/2023
 * @Version 1.0.0
 */
public class EnhancedCaching implements InvocationHandler {

    private static final Class<?> DEFAULT_CACHE_INTERFACE_CLASS = MockCache.class;
    private static final Method EVICT_METHOD = MethodUtils.getMatchingAccessibleMethod(Cache.class, "evict", Object.class);
    private static final Method CLEAR_METHOD = MethodUtils.getMatchingAccessibleMethod(Cache.class, "clear");

    private final String cacheName;
    private final Cache cache;
    private final CacheMethodFilter cacheMethodFilter;

    EnhancedCaching(String cacheName, Cache cache, CacheMethodFilter cacheMethodFilter) {
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

        if (methodName.equals("justEvict")) {
            return EVICT_METHOD.invoke(cache, args);
        } else if (methodName.equals("justClear")) {
            return CLEAR_METHOD.invoke(cache, args);
        } else {
            Object result = method.invoke(cache, args);
            switch (methodName) {
                case "put":
                    cacheMethodFilter.onPut(cacheName, args[0]);
                    break;
                case "evict":
                    cacheMethodFilter.onEvict(cacheName, args[0]);
                    break;
                case "get":
                    cacheMethodFilter.onGet(cacheName, args[0], result);
                    break;
                case "clear":
                    cacheMethodFilter.onClear(cacheName);
                    break;
            }
            return result;
        }
    }

    public static Cache createProxy(String cacheName, Cache cacheObject,
                                    CacheMethodFilter cacheMethodFilter) {
        return (MockCache) Proxy.newProxyInstance(cacheObject.getClass().getClassLoader(),
                new Class<?>[]{DEFAULT_CACHE_INTERFACE_CLASS},
                new EnhancedCaching(cacheName, cacheObject, cacheMethodFilter));
    }
}