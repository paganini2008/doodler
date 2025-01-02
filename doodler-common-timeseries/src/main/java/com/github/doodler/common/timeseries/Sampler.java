package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: Sampler
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
public interface Sampler<E> {

    long getTimestamp();

    E getSample();

}
