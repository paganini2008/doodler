package com.github.doodler.common.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: SerializableTaskTimer
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
public class SerializableTaskTimer extends SimpleTimer {

    public SerializableTaskTimer(long initialDelay, long period, TimeUnit timeUnit) {
        super(initialDelay, period, timeUnit);
    }

    private final List<Runnable> runners = new CopyOnWriteArrayList<>();

    public void addBatch(Runnable r) {
        if (r != null) {
            runners.add(r);
        }
    }

    public void remove(Runnable r) {
        if (r != null) {
            runners.remove(r);
        }
    }

    public int batchSize() {
        return runners.size();
    }

    @Override
    public boolean change() throws Exception {
        if (!runners.isEmpty()) {
        	runners.forEach(r -> r.run());
        }
        return true;
    }
}