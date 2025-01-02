package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: SamplerImpl
 * @Author: Fred Feng
 * @Date: 14/11/2024
 * @Version 1.0.0
 */
public class SamplerImpl<E extends Metric> implements Sampler<E> {

    private final long timestamp;
    private final E sample;

    public SamplerImpl(long timestamp, E sample) {
        this.timestamp = timestamp;
        this.sample = sample;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public E getSample() {
        return sample;
    }
}