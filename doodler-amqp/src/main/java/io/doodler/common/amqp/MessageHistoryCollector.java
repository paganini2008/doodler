package io.doodler.common.amqp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.doodler.common.utils.LruList;
import io.doodler.common.utils.MapUtils;

/**
 * @Description: MessageHistoryCollector
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
public final class MessageHistoryCollector {

    private static final int DEFAULT_MAX_MESSAGE_HISTORY_SIZE = 128;
    private final Map<String, List<Object>> pushHistory = new ConcurrentHashMap<>();
    private final Map<String, List<Object>> pullHistory = new ConcurrentHashMap<>();

    public void push(String eventName, Object payload) {
        List<Object> list = MapUtils.getOrCreate(pushHistory, eventName,
                () -> new LruList<>(DEFAULT_MAX_MESSAGE_HISTORY_SIZE));
        list.add(payload);
    }

    public void pull(String eventName, Object payload) {
        List<Object> list = MapUtils.getOrCreate(pullHistory, eventName,
                () -> new LruList<>(DEFAULT_MAX_MESSAGE_HISTORY_SIZE));
        list.add(payload);
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
}