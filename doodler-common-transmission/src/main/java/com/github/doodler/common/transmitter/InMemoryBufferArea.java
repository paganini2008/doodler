package com.github.doodler.common.transmitter;

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
public class InMemoryBufferArea implements BufferArea, RemovalListener<Packet> {

    private final int maxSize;
    private final BufferArea overflowArea;

    private final Map<String, List<Packet>> cache;

    public InMemoryBufferArea() {
        this(256, null);
    }

    public InMemoryBufferArea(int maxSize, BufferArea overflowArea) {
        this.maxSize = maxSize;
        this.overflowArea = overflowArea;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String collection, Packet packet) {
        List<Packet> list = MapUtils.getOrCreate(cache, collection,
                () -> new NamedLruList(collection, maxSize, this));
        list.add(packet);
    }

    @Override
    public long size(String collection) {
        long n = 0;
        List<Packet> list = cache.get(collection);
        if (list != null) {
            n += list.size();
        }
        if (overflowArea != null) {
            n += overflowArea.size(collection);
        }
        return n;
    }

    @Override
    public Packet poll(String collection) {
        if (overflowArea != null && overflowArea.size(collection) > 0) {
            return overflowArea.poll(collection);
        }
        List<Packet> list = cache.get(collection);
        return CollectionUtils.isNotEmpty(list) ? list.remove(0) : null;
    }

    @Override
    public Collection<Packet> poll(String collection, int batchSize) {
        if (batchSize == 1) {
            return Collections.singletonList(poll(collection));
        }
        if (overflowArea != null && overflowArea.size(collection) > 0) {
            return overflowArea.poll(collection, batchSize);
        }
        List<Packet> list = cache.get(collection);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Packet> sublist = list.subList(0, Math.min(batchSize, list.size()));
            list.removeAll(sublist);
            return Collections.unmodifiableCollection(sublist);
        }
        return Collections.emptyList();
    }

    @Override
    public void onRemoval(Object elderKey, Packet elderValue) {
        if (overflowArea == null) {
            return;
        }
        String collection = (String) elderKey;
        overflowArea.put(collection, elderValue);
    }

    /**
     * 
     * @Description: NamedLruList
     * @Author: Fred Feng
     * @Date: 28/12/2024
     * @Version 1.0.0
     */
    private static class NamedLruList extends LruList<Packet> {

        public NamedLruList(final String collection, final int maxSize,
                final RemovalListener<Packet> removalListener) {
            super(new CopyOnWriteArrayList<>(), maxSize, new RemovalListener<Packet>() {
                @Override
                public void onRemoval(Object elderKey, Packet elderValue) {
                    removalListener.onRemoval(collection, elderValue);
                }
            });
        }

        private static final long serialVersionUID = 1L;
    }


}
