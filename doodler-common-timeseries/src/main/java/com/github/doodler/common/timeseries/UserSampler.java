package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: DefaultSampler
 * @Author: Fred Feng
 * @Date: 14/11/2024
 * @Version 1.0.0
 */
public interface UserSampler<E extends UserMetric<E>> extends Sampler<E> {

    E merge(E update);

    E reset(E update);

}
