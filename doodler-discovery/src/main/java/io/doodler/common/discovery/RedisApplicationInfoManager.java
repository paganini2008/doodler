package io.doodler.common.discovery;

import static io.doodler.common.discovery.DiscoveryClientConstants.REDIS_KEY_PATTERN_DISCOVERY_CLIENT;
import static io.doodler.common.discovery.DiscoveryClientConstants.REDIS_KEY_PREFIX_DISCOVERY_CLIENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.doodler.common.utils.JacksonUtils;
import io.doodler.common.utils.MapUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RedisApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 04/09/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisApplicationInfoManager implements ApplicationInfoManager {

    private final StringRedisTemplate redisOperations;

    private final ApplicationInfoHolder applicationInfoHolder;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public ApplicationInfo registerSelf() {
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        String key = String.format(REDIS_KEY_PATTERN_DISCOVERY_CLIENT, applicationName);
        String json = JacksonUtils.toJsonString(applicationInfo);
        Long index = redisOperations.opsForList().indexOf(key, json);
        if (index == null) {
            redisOperations.opsForList().leftPush(key, json);
            if (log.isInfoEnabled()) {
                log.info("Add application instance: '{}' to '{}'", applicationInfo, applicationName);
            }
        }
        return applicationInfo;
    }

    @Override
    public List<ApplicationInfo> cleanExpiredSiblings(Heartbeater heartbeater) {
        String key = String.format(REDIS_KEY_PATTERN_DISCOVERY_CLIENT, applicationName);
        List<String> list = redisOperations.opsForList().range(key, 0, -1);
        List<ApplicationInfo> expiredList = new ArrayList<>();
        list.stream().map(str -> JacksonUtils.parseJson(str, ApplicationInfo.class))
                .filter(info -> applicationInfoHolder.get().hasSibling(info)).forEach(info -> {
                    if (!heartbeater.isAlive(info)) {
                        String jsonString = JacksonUtils.toJsonString(info);
                        redisOperations.opsForList().remove(key, 1, jsonString);
                        if (log.isInfoEnabled()) {
                            log.info("Remove expired application '{}' from '{}'", info, info.getApplicationName());
                        }
                        expiredList.add(info);
                    }
                });
        return expiredList;
    }

    @Override
    public Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includedSelf) {
        Map<String, Collection<ApplicationInfo>> map = new ConcurrentHashMap<>();
        Set<String> keys = redisOperations.keys(REDIS_KEY_PREFIX_DISCOVERY_CLIENT + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        keys.stream().flatMap(key -> redisOperations.opsForList().range(key, 0, -1).stream())
                .map(data -> JacksonUtils.parseJson(data, ApplicationInfo.class))
                .filter(info -> includedSelf || !info.getApplicationName().equals(applicationName)).forEach(info -> {
                    Collection<ApplicationInfo> instances = MapUtils.getOrCreate(map, info.getApplicationName(),
                            CopyOnWriteArrayList::new);
                    instances.add(info);
                });
        return map;
    }

    @Override
    public Collection<ApplicationInfo> getSiblingApplicationInfos() {
        String key = String.format(REDIS_KEY_PATTERN_DISCOVERY_CLIENT, applicationName);
        List<String> values = redisOperations.opsForList().range(key, 0, -1);
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(data -> JacksonUtils.parseJson(data, ApplicationInfo.class))
                .filter(info -> applicationInfoHolder.get().hasSibling(info)).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }


}