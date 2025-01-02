package com.github.doodler.common.timeseries;

/**
 * 
 * @Description: NumberMetric
 * @Author: Fred Feng
 * @Date: 15/11/2024
 * @Version 1.0.0
 */
public interface NumberMetric<T extends Number> extends UserMetric<NumberMetric<T>> {

    T getHighestValue();

    T getLowestValue();

    T getTotalValue();

    T getBaseValue();

    long getCount();

    long getBaseCount();

    Double getAverageValue();

}
