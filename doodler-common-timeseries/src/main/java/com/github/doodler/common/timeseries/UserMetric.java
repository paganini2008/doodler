package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: UserMetric
 * @Author: Fred Feng
 * @Date: 14/11/2024
 * @Version 1.0.0
 */
public interface UserMetric<T> extends Metric {

    T merge(T update);

    T reset(T update);

}
