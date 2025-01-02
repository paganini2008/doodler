package com.github.doodler.common.timeseries;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 
 * @Description: UserSamplerImpl
 * @Author: Fred Feng
 * @Date: 14/11/2024
 * @Version 1.0.0
 */
public class UserSamplerImpl<T extends UserMetric<T>> implements UserSampler<T> {

    private final AtomicStampedReference<T> ref = new AtomicStampedReference<T>(null, 0);
    private long timestamp;
    private T NULL;

    public UserSamplerImpl(long timestamp, T NULL) {
        this.timestamp = timestamp;
        this.NULL = NULL;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public T merge(T value) {
        T current;
        T update;
        do {
            current = ref.getReference();
            update = current != null ? current.merge(value) : value;
        } while (!ref.compareAndSet(current, update, ref.getStamp(), ref.getStamp() + 1));
        this.timestamp = System.currentTimeMillis();
        return update;
    }

    @Override
    public T reset(T value) {
        T current;
        T update;
        do {
            current = ref.getReference();
            update = current != null ? current.reset(value) : value;
        } while (!ref.compareAndSet(current, update, ref.getStamp(), ref.getStamp() + 1));
        this.timestamp = System.currentTimeMillis();
        return update;
    }

    @Override
    public T getSample() {
        T t = ref.getReference();
        if (t == null) {
            t = NULL;
        }
        return t;
    }

}
