package com.github.doodler.common.feign.statistics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.github.doodler.common.timeseries.Sampler;
import com.github.doodler.common.timeseries.StringSamplerService;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.SimpleTimer;

/**
 * @Description: TpsCalculator
 * @Author: Fred Feng
 * @Date: 22/09/2023
 * @Version 1.0.0
 */
public class TpsCalculator extends SimpleTimer {

    public TpsCalculator(long period, TimeUnit timeUnit, StringSamplerService<HttpSample> service) {
        super(period, timeUnit);
        this.service = service;
    }

    private final StringSamplerService<HttpSample> service;
    private final Map<String, Map<String, TpsUpdater>> cache = new ConcurrentHashMap<>();

    public void incr(String name, String identifier) {
        Map<String, TpsUpdater> map = MapUtils.getOrCreate(cache, name, ConcurrentHashMap::new);
        TpsUpdater updater = MapUtils.getOrCreate(map, identifier, SimpleTpsUpdater::new);
        updater.incr();
    }

    public int get(String name, String identifier) {
        TpsUpdater updater = null;
        if (cache.containsKey(name)) {
            updater = cache.get(name).get(identifier);
        }
        return updater != null ? updater.get() : 0;
    }

    @Override
    public boolean change() throws Exception {
        if (cache.size() > 0) {
            for (String name : cache.keySet()) {
                cache.get(name).entrySet().forEach(e -> {
                    e.getValue().set();
                    int tps = e.getValue().get();
                    if(tps > 0) {
                        Sampler<HttpSample> sampler = service.sampler(name, e.getKey(), System.currentTimeMillis());
                        if (sampler != null) {
                            sampler.getSample().tps = tps;
                        }
                    }
                });
            }
        }
        return true;
    }
}