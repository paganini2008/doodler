package com.github.doodler.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @Description: ThreadLocalInteger
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
public class ThreadLocalInteger extends Number {

    private static final long serialVersionUID = -8497249965680057815L;

    private final ThreadLocal<Integer> threadLocal;

    public ThreadLocalInteger() {
        this(0);
    }

    public ThreadLocalInteger(final int value) {
        threadLocal = TransmittableThreadLocal.withInitial(() -> value);
    }

    public int getAndDecrement() {
        return getAndAdd(-1);
    }

    public int getAndIncrement() {
        return getAndAdd(1);
    }

    public int getAndAdd(int delta) {
        int prev = get();
        threadLocal.set(prev + delta);
        return prev;
    }

    public int incrementAndGet() {
        return addAndGet(1);
    }

    public int addAndGet(int delta) {
        int prev = get();
        threadLocal.set(prev + delta);
        return threadLocal.get();
    }

    public int decrementAndGet() {
        return addAndGet(-1);
    }

    public void reset() {
        threadLocal.remove();
    }

    public void set(int delta) {
        threadLocal.set(delta);
    }

    public int get() {
        return threadLocal.get().intValue();
    }

    @Override
    public int intValue() {
        return get();
    }

    @Override
    public long longValue() {
        return (long) get();
    }

    @Override
    public float floatValue() {
        return (float) get();
    }

    @Override
    public double doubleValue() {
        return (double) get();
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }
}