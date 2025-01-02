package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import com.github.doodler.common.utils.MapUtils;

/**
 * 
 * @Description: SamplerService
 * @Author: Fred Feng
 * @Date: 15/11/2024
 * @Version 1.0.0
 */
public abstract class SamplerService<C, D, E extends Metric, T extends Sampler<E>> {

    private final Map<C, Map<D, SampleCollector<E, T>>> data = new ConcurrentHashMap<>();
    private final Map<C, Map<D, T>> total = new ConcurrentHashMap<>();

    protected void collect(C category, D dimension, long timestamp, Consumer<T> consumer) {
        Map<D, SampleCollector<E, T>> collectors =
                MapUtils.getOrCreate(data, category, ConcurrentHashMap::new);
        SampleCollector<E, T> sampleCollector = MapUtils.getOrCreate(collectors, dimension,
                () -> getSampleCollector(category, dimension, timestamp));
        consumer.accept(sampleCollector.sampler(timestamp));

        Map<D, T> samplers = MapUtils.getOrCreate(total, category, ConcurrentHashMap::new);
        T sampler = MapUtils.getOrCreate(samplers, dimension,
                () -> getEmptySampler(category, dimension, timestamp));
        consumer.accept(sampler);
    }

    public Collection<C> categories() {
        return Collections.unmodifiableCollection(data.keySet());
    }

    public Collection<D> dimensions(C category) {
        return Collections.unmodifiableCollection(Optional.ofNullable(data.get(category))
                .orElseGet(() -> Collections.emptyMap()).keySet());
    }

    public Map<Instant, Object> sequence(C category, D dimension) {
        SampleCollector<E, T> sampleCollector = Optional.ofNullable(data.get(category))
                .orElseGet(() -> Collections.emptyMap()).get(dimension);
        Map<Instant, Object> data = sampleCollector != null
                ? sampleCollector.samplers().entrySet().stream().collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().getSample().represent()),
                        LinkedHashMap::putAll)
                : Collections.emptyMap();
        return data;
    }

    public Map<D, Object> rank(C category, Comparator<T> comparator, int topN) {
        if (data.containsKey(category)) {
            final long now = System.currentTimeMillis();
            Map<D, T> map = data.get(category).entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), e.getValue().sampler(now)), LinkedHashMap::putAll);
            return map.entrySet().stream().sorted((a, b) -> {
                return comparator.compare(a.getValue(), b.getValue());
            }).limit(topN).collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), e.getValue().getSample().represent()),
                    LinkedHashMap::putAll);
        }
        return Collections.emptyMap();
    }

    public T sampler(C category, D dimension) {
        return sampler(category, dimension, System.currentTimeMillis());
    }

    public T sampler(C category, D dimension, long timestamp) {
        SampleCollector<E, T> sampleCollector = Optional.ofNullable(data.get(category))
                .orElseGet(() -> Collections.emptyMap()).get(dimension);
        return sampleCollector != null ? sampleCollector.sampler(timestamp) : null;
    }

    public Map<D, T> summarize(C category) {
        Map<D, T> results = new LinkedHashMap<>();
        Optional.ofNullable(total.get(category)).ifPresent(m -> {
            for (Map.Entry<D, T> e : m.entrySet()) {
                results.put(e.getKey(), e.getValue());
            }
        });
        return results;
    }

    public T summarize(C category, D dimension) {
        return Optional.ofNullable(total.get(category)).orElseGet(() -> Collections.emptyMap())
                .get(dimension);
    }

    protected abstract SampleCollector<E, T> getSampleCollector(C category, D dimension,
            long timestamp);

    protected abstract T getEmptySampler(C category, D dimension, long timestamp);

}
