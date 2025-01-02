package com.github.doodler.common.timeseries;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
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

    private static final long RETAIN_MAXIMUM_SIZE = 7L * 24 * 60;
    private static final String REDIS_KEY_PATTERN = "%s:%s:%s";
    private final String namespace;
    private final RedisTemplate<String, Object> redisOperations;

    @Override
    public void persist(String category, String dimension, Instant instant, T data) {
        super.persist(category, dimension, instant, data);
        String key = String.format(REDIS_KEY_PATTERN, namespace, category, dimension);
        Long size = redisOperations.opsForHash().size(key);
        if (size == null) {
            size = 0L;
        }
        if (size < RETAIN_MAXIMUM_SIZE) {
            redisOperations.opsForHash().put(key, String.valueOf(instant.toEpochMilli()),
                    data.represent());
        }
    }

    @Override
    public Map<Instant, Object> retrive(String category, String dimension) {
        String key = String.format(REDIS_KEY_PATTERN, namespace, category, dimension);
        Map<Object, Object> entries = redisOperations.opsForHash().entries(key);
        Map<Instant, Object> results = entries.entrySet().stream()
                .collect(Collectors.toMap(
                        a -> Instant.ofEpochMilli(Long.valueOf(a.getKey().toString())),
                        Map.Entry::getValue, (a, b) -> a, TreeMap::new));
        return Collections.unmodifiableMap(results);
    }

    @Override
    public void clean(String category) {
        String keyPattern = String.format(REDIS_KEY_PATTERN, namespace, category, "*");
        RedisKeyIterator iterator =
                new RedisKeyIterator(keyPattern, redisOperations.getConnectionFactory());
        while (iterator.hasNext()) {
            String key = iterator.next();
            redisOperations.delete(key);
        }
    }

    @Override
    public void clean(String category, String dimension) {
        String key = String.format(REDIS_KEY_PATTERN, namespace, category, dimension);
        redisOperations.delete(key);
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
