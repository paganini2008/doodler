package com.github.doodler.common.transmission;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.collections4.CollectionUtils;
import com.github.doodler.common.utils.LruList;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.RemovalListener;

/**
 * 
 * @Description: InMemoryBufferArea
 * @Author: Fred Feng
 * @Date: 26/12/2024
 * @Version 1.0.0
 */
public class InMemoryBufferArea implements BufferArea, RemovalListener<Bucket> {

    private final int maxSize;
    private final BufferArea overflowArea;

    private final Map<String, List<Bucket>> cache;

    public InMemoryBufferArea() {
        this(256, null);
    }

    public InMemoryBufferArea(int maxSize, BufferArea overflowArea) {
        this.maxSize = maxSize;
        this.overflowArea = overflowArea;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String collection, Bucket bucket) {
        List<Bucket> list = MapUtils.getOrCreate(cache, collection,
                () -> new NamedLruList(collection, maxSize, this));
        list.add(bucket);
    }

    @Override
    public long size(String collection) {
        long n = 0;
        List<Bucket> list = cache.get(collection);
        if (list != null) {
            n += list.size();
        }
        if (overflowArea != null) {
            n += overflowArea.size(collection);
        }
        return n;
    }

    @Override
    public Bucket poll(String collection) {
        if (overflowArea != null && overflowArea.size(collection) > 0) {
            return overflowArea.poll(collection);
        }
        List<Bucket> list = cache.get(collection);
        return CollectionUtils.isNotEmpty(list) ? list.remove(0) : null;
    }

    @Override
    public Collection<Bucket> poll(String collection, int batchSize) {
        if (batchSize == 1) {
            return Collections.singletonList(poll(collection));
        }
        if (overflowArea != null && overflowArea.size(collection) > 0) {
            return overflowArea.poll(collection, batchSize);
        }
        List<Bucket> list = cache.get(collection);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Bucket> sublist = list.subList(0, Math.min(batchSize, list.size()));
            list.removeAll(sublist);
            return Collections.unmodifiableCollection(sublist);
        }
        return Collections.emptyList();
    }

    @Override
    public void onRemoval(Object elderKey, Bucket elderValue) {
        if (overflowArea == null) {
            return;
        }
        String collection = (String) elderKey;
        overflowArea.put(collection, elderValue);
    }

    private static class NamedLruList extends LruList<Bucket> {

        public NamedLruList(final String collection, final int maxSize,
                final RemovalListener<Bucket> removalListener) {
            super(new CopyOnWriteArrayList<>(), maxSize, new RemovalListener<Bucket>() {
                @Override
                public void onRemoval(Object elderKey, Bucket elderValue) {
                    removalListener.onRemoval(collection, elderValue);
                }
            });
        }

        private static final long serialVersionUID = 1L;
    }


}
