package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @Description: OverflowDataStore
 * @Author: Fred Feng
 * @Date: 13/11/2024
 * @Version 1.0.0
 */
public class OverflowDataStore<C, D, E extends Metric, T extends Sampler<E>> implements OverflowRemovalListener<T> {

    private final C category;
    private final D dimension;

    public OverflowDataStore(C category, D dimension, List<OverflowDataHandler<C, D, E>> overflowDataHandlers) {
        this.category = category;
        this.dimension = dimension;
        this.overflowDataHandlers = overflowDataHandlers;
    }

    private final List<OverflowDataHandler<C, D, E>> overflowDataHandlers;

    @Override
    public void onRemoval(Object elderKey, T elderValue) {
        Optional.ofNullable(overflowDataHandlers).ifPresent(a -> {
            a.forEach(h -> {
                h.persist(category, dimension, (Instant) elderKey, elderValue.getSample());
            });
        });
    }

}
