package com.github.doodler.common.timeseries;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: RedisOverflowDataManager
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RedisOverflowDataManager<T extends Metric>
        extends LoggingOverflowDataHandler<String, String, T> implements OverflowDataManager {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int RETAIN_MAXIMUM_DAYS = 7;
    private static final String REDIS_KEY_PATTERN = "%s:%s:%s:%s";
    private final String namespace;
    private final RedisTemplate<String, Object> redisOperations;

    @Override
    public void persist(String category, String dimension, Instant instant, T data) {
        super.persist(category, dimension, instant, data);
        if (getDays(category, dimension).size() > RETAIN_MAXIMUM_DAYS) {
            return;
        }
        String date = instant.atZone(ZoneId.systemDefault()).toLocalDate().format(dtf);
        String key = String.format(REDIS_KEY_PATTERN, namespace, category, dimension, date);
        redisOperations.opsForHash().put(key, String.valueOf(instant.toEpochMilli()),
                data.represent());

    }

    @Override
    public List<String> getDays(String category, String dimension) {
        String keyPattern = String.format(REDIS_KEY_PATTERN, namespace, category, dimension, "*");
        Set<String> keys = redisOperations.keys(keyPattern);
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        return keys.stream().map(k -> k.split(":")[3]).toList();
    }

    @Override
    public Map<Instant, Object> retrieve(String category, String dimension, int N) {
        String keyPattern = String.format(REDIS_KEY_PATTERN, namespace, category, dimension, "*");
        RedisKeyIterator iterator =
                new RedisKeyIterator(keyPattern, redisOperations.getConnectionFactory());
        Map<Instant, Object> results = new TreeMap<>(Comparator.reverseOrder());
        while (iterator.hasNext()) {
            String key = iterator.next();
            Map<Object, Object> entries = redisOperations.opsForHash().entries(key);
            Map<Instant, Object> map = entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            a -> Instant.ofEpochMilli(Long.valueOf(a.getKey().toString())),
                            Map.Entry::getValue, (a, b) -> a, HashMap::new));
            results.putAll(map);
        }
        results = results.entrySet().stream().limit(Integer.min(results.size(), N))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                        LinkedHashMap::new));
        return Collections.unmodifiableMap(results);
    }

    @Override
    public void clean(String category) {
        String keyPattern = String.format("%s:%s:%s", namespace, category, "*");
        RedisKeyIterator iterator =
                new RedisKeyIterator(keyPattern, redisOperations.getConnectionFactory());
        while (iterator.hasNext()) {
            String key = iterator.next();
            redisOperations.delete(key);
        }
    }

    @Override
    public void clean(String category, String dimension) {
        String keyPattern = String.format(REDIS_KEY_PATTERN, namespace, category, dimension, "*");
        RedisKeyIterator iterator =
                new RedisKeyIterator(keyPattern, redisOperations.getConnectionFactory());
        while (iterator.hasNext()) {
            String key = iterator.next();
            redisOperations.delete(key);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public RedisOperations<String, Object> getRedisOperations() {
        return redisOperations;
    }

    /**
     * 
     * @Description: RedisKeyIterator
     * @Author: Fred Feng
     * @Date: 02/01/2025
     * @Version 1.0.0
     */
    private static class RedisKeyIterator implements Iterator<String> {

        RedisKeyIterator(String keyPattern, RedisConnectionFactory redisConnectionFactory) {
            ScanOptions options = ScanOptions.scanOptions().match(keyPattern).build();
            RedisConnection connection = redisConnectionFactory.getConnection();
            this.cursor = connection.scan(options);
        }

        private final Cursor<byte[]> cursor;

        @Override
        public boolean hasNext() {
            return cursor.hasNext();
        }

        @Override
        public String next() {
            return new String(cursor.next(), StandardCharsets.UTF_8);
        }
    }

}
