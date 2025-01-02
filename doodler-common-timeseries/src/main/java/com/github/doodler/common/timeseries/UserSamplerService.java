package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: UserSamplerService
 * @Author: Fred Feng
 * @Date: 15/11/2024
 * @Version 1.0.0
 */
public abstract class UserSamplerService<C, D, E extends UserMetric<E>, T extends UserSampler<E>> extends
        SamplerService<C, D, E, T> {

    public void increase(C name, D dimension, long timestamp, E update) {
        super.collect(name, dimension, timestamp, a -> {
            a.merge(update);
        });
    }

    public void decrease(C name, D dimension, long timestamp, E update) {
        super.collect(name, dimension, timestamp, a -> {
            a.reset(update);
        });
    }

}
