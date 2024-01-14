package com.github.doodler.common.utils.statistics;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: StatisticsService
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public abstract class StatisticsService<T> {

    private final Map<String, Map<String, SampleCollector<T>>> data = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Sampler<T>>> total = new ConcurrentHashMap<>();

    public void prepare(String name, String identifier, long timestampMillis, Consumer<Sampler<T>> consumer) {
        Map<String, SampleCollector<T>> collectors = MapUtils.getOrCreate(data, name, ConcurrentHashMap::new);
        SampleCollector<T> sampleCollector = MapUtils.getOrCreate(collectors, identifier,
                () -> getSampleCollector(timestampMillis));
        consumer.accept(sampleCollector.sampler(timestampMillis));

        Map<String, Sampler<T>> samplers = MapUtils.getOrCreate(total, name, ConcurrentHashMap::new);
        Sampler<T> sampler = MapUtils.getOrCreate(samplers, identifier, () -> getEmptySampler(timestampMillis));
        consumer.accept(sampler);
    }

    public void update(String name, String identifier, long timestampMillis, Consumer<Sampler<T>> consumer) {
        prepare(name, identifier, timestampMillis, consumer);
    }
    
    public Map<String, Sampler<T>> rank(String name, Comparator<Sampler<T>> comparator, int topN) {
        if (data.containsKey(name)) {
        	long now = System.currentTimeMillis();
            Map<String, Sampler<T>> map = data.get(name).entrySet().stream().collect(LinkedHashMap::new,
                    (m, e) -> m.put(e.getKey(), e.getValue().sampler(now)), LinkedHashMap::putAll);
            return map.entrySet().stream().sorted((a, b) -> {
                return comparator.compare(a.getValue(), b.getValue());
            }).limit(topN).collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
        }
        return Collections.emptyMap();
    }

    public Collection<String> identifiers(String name) {
        if (data.containsKey(name)) {
            return Collections.unmodifiableCollection(data.get(name).keySet());
        }
        return Collections.emptyList();
    }

    public Map<String, Sampler<T>> sequence(String name, String identifier) {
        SampleCollector<T> sampleCollector = null;
        if (data.containsKey(name)) {
            sampleCollector = data.get(name).get(identifier);
        }
        return sampleCollector != null ? sampleCollector.samplers() : Collections.emptyMap();
    }
    
    public Sampler<T> sampler(String name, String identifier){
    	return sampler(name, identifier, System.currentTimeMillis());
    }

    public Sampler<T> sampler(String name, String identifier, long timestampMillis) {
        SampleCollector<T> sampleCollector = null;
        if (data.containsKey(name)) {
            sampleCollector = data.get(name).get(identifier);
        }
        return sampleCollector != null ? sampleCollector.sampler(timestampMillis) : getEmptySampler(timestampMillis);
    }

    public Map<String, Sampler<T>> summarize(String name) {
        Map<String, Sampler<T>> map = new LinkedHashMap<>();
        for (String identifier : identifiers(name)) {
            map.put(identifier, summarize(name, identifier));
        }
        return map;
    }

    public Sampler<T> summarize(String name, String identifier) {
    	Sampler<T> sampler = null;
        if (total.containsKey(name)) {
        	sampler = total.get(name).get(identifier);
        }
        if(sampler == null) {
        	sampler = getEmptySampler(System.currentTimeMillis());
        }
        return sampler;
    }

    protected abstract SampleCollector<T> getSampleCollector(long timestampMillis);

    protected abstract Sampler<T> getEmptySampler(long timestampMillis);
}