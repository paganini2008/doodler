package io.doodler.common.utils;
/**
 * @Description: SimpleRetryQueue
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description: SimpleRetryQueue
 * @Author: Fred Feng
 * @Date: 18/02/2023
 * @Version 1.0.0
 */
public class SimpleRetryQueue implements RetryQueue {

    private final List<Object> cache;

    public SimpleRetryQueue() {
        this(new CopyOnWriteArrayList<>());
    }

    public SimpleRetryQueue(List<Object> cache) {
        this.cache = cache;
    }

    public void putObject(Object object) {
        cache.add(object);
    }

    public void removeObject(Object object) {
        cache.remove(object);
    }

    public int size() {
        return cache.size();
    }

    public void drainTo(Collection<Object> output) {
        final int size = size();
        if (size > 0) {
            List<Object> subList = cache.subList(0, size);
            output.addAll(subList);
            cache.removeAll(subList);
        }
    }
}