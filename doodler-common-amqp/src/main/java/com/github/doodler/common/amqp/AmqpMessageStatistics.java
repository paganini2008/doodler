package com.github.doodler.common.amqp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.github.doodler.common.utils.LruList;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: AmqpMessageStatistics
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
public final class AmqpMessageStatistics {

    private static final int DEFAULT_MAX_MESSAGE_HISTORY_SIZE = 128;
    private final Map<String, List<Object>> pushHistory = new ConcurrentHashMap<>();
    private final Map<String, List<Object>> pullHistory = new ConcurrentHashMap<>();
    private final Map<String, List<Object>> errorHistory = new ConcurrentHashMap<>();
    private final AtomicLong pushCounter = new AtomicLong();
    private final AtomicLong pullCounter = new AtomicLong();
    private final AtomicLong errorCounter = new AtomicLong();

    public long push(String eventName, Object payload) {
        List<Object> list = MapUtils.getOrCreate(pushHistory, eventName,
                () -> new LruList<>(DEFAULT_MAX_MESSAGE_HISTORY_SIZE));
        list.add(payload);
        return pushCounter.incrementAndGet();
    }

    public long pull(String eventName, Object payload) {
        List<Object> list = MapUtils.getOrCreate(pullHistory, eventName,
                () -> new LruList<>(DEFAULT_MAX_MESSAGE_HISTORY_SIZE));
        list.add(payload);
        return pullCounter.incrementAndGet();
    }
    
    public long error(String eventName, Object payload) {
        List<Object> list = MapUtils.getOrCreate(errorHistory, eventName,
                () -> new LruList<>(DEFAULT_MAX_MESSAGE_HISTORY_SIZE));
        list.add(payload);
        return errorCounter.incrementAndGet();
    }

    public Map<String, List<Object>> getPushHistory() {
        return pushHistory.entrySet().stream().collect(HashMap::new, (m, e) -> {
            List<Object> list = new ArrayList<>(e.getValue());
            Collections.reverse(list);
            m.put(e.getKey(), list);
        }, HashMap::putAll);
    }

    public Map<String, List<Object>> getPullHistory() {
        return pullHistory.entrySet().stream().collect(HashMap::new, (m, e) -> {
            List<Object> list = new ArrayList<>(e.getValue());
            Collections.reverse(list);
            m.put(e.getKey(), list);
        }, HashMap::putAll);
    }
    
    public Map<String, List<Object>> getErrorHistory() {
        return errorHistory.entrySet().stream().collect(HashMap::new, (m, e) -> {
            List<Object> list = new ArrayList<>(e.getValue());
            Collections.reverse(list);
            m.put(e.getKey(), list);
        }, HashMap::putAll);
    }
    
    public long getPushCount() {
    	return pushCounter.get();
    }
    
    public long getPullCount() {
    	return pullCounter.get();
    }
    
    public long getErrorCount() {
    	return errorCounter.get();
    }
}