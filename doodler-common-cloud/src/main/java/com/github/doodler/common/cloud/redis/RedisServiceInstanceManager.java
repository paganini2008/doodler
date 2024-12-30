package com.github.doodler.common.cloud.redis;

import static com.github.doodler.common.cloud.redis.CloudConstants.DOWN;
import static com.github.doodler.common.cloud.redis.CloudConstants.UP;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import com.github.doodler.common.cloud.SiblingApplicationCondition;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.SimpleTimer;
import lombok.Setter;

/**
 * 
 * @Description: RedisServiceInstanceManager
 * @Author: Fred Feng
 * @Date: 09/08/2024
 * @Version 1.0.0
 */
public class RedisServiceInstanceManager extends SimpleTimer
        implements ApplicationEventPublisherAware, ServiceInstanceManager {

    final static String SERVICE_INSTANCE_KEY = "services:instance:";
    final static String SERVICE_INSTANCE_KEY_PREFIX = SERVICE_INSTANCE_KEY + "%s";

    private final RedisTemplate<String, ApplicationInstance> redisTemplate;
    private final Ping ping;
    private final SiblingApplicationCondition siblingApplicationCondition;

    private final Map<String, Set<String>> keepAliveMap = new HashMap<>();
    private final Map<String, Set<String>> maintainanceMap = new HashMap<>();

    public RedisServiceInstanceManager(RedisConnectionFactory redisConnectionFactory,
            int keepAliveInterval, Ping ping,
            SiblingApplicationCondition siblingApplicationCondition) {
        super(keepAliveInterval, keepAliveInterval, TimeUnit.SECONDS);

        Jackson2JsonRedisSerializer<ApplicationInstance> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(ApplicationInstance.class);
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        this.ping = ping;
        this.siblingApplicationCondition = siblingApplicationCondition;
    }

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    private ApplicationInstance selfInstance;

    @Override
    public List<ServiceInstance> getInstancesByServiceId(String serviceId) {
        String key = getKey(serviceId);
        List<ApplicationInstance> applicationInstances =
                redisTemplate.opsForList().range(key, 0, -1);
        if (CollectionUtils.isNotEmpty(applicationInstances)) {
            return applicationInstances.stream().filter(i -> UP.equals(getInstanceStatus(i)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public ServiceInstance getInstanceByServiceIdAndHost(String serviceId, String host, int port) {
        List<ServiceInstance> applicationInstances = getInstancesByServiceId(serviceId);
        return applicationInstances.stream()
                .filter(i -> i.getHost().equals(host) && i.getPort() == port).findFirst()
                .orElse(null);
    }

    @Override
    public Collection<String> getServiceNames() {
        String keyPattern = String.format(SERVICE_INSTANCE_KEY_PREFIX, "*");
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            return keys.stream().map(key -> key.replace(SERVICE_INSTANCE_KEY, ""))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<ServiceInstance>> getServices() {
        Collection<String> serviceNames = getServiceNames();
        if (CollectionUtils.isEmpty(serviceNames)) {
            return Collections.emptyMap();
        }
        return serviceNames.stream().collect(HashMap::new,
                (m, serviceId) -> m.put(serviceId, getInstancesByServiceId(serviceId)),
                HashMap::putAll);
    }

    @Override
    public void registerInstance(ServiceInstance instance) {
        ApplicationInstance applicationInstance = (ApplicationInstance) instance;
        String key = getKey(applicationInstance.getServiceId());
        redisTemplate.opsForList().leftPush(key, applicationInstance);
        if (log.isInfoEnabled()) {
            log.info("Register new application instance '{}'", instance);
        }
        this.selfInstance = applicationInstance;
        applicationEventPublisher
                .publishEvent(new InstanceStatusChangeEvent(instance, InstanceStatus.NEW));
    }

    @Override
    public void deregisterInstance(ServiceInstance instance) {
        String key = getKey(instance.getServiceId());
        List<ApplicationInstance> instances = redisTemplate.opsForList().range(key, 0, -1);
        if (instances.remove(instance)) {
            redisTemplate.opsForList().remove(key, 1, instance);
        }
        applicationEventPublisher.publishEvent(
                new InstanceStatusChangeEvent(instance, InstanceStatus.OUT_OF_SERVICE));
    }

    @Override
    public void updateMetadata(ServiceInstance instance, Map<String, String> metadata) {
        String key = getKey(instance.getServiceId());
        List<ApplicationInstance> instances = redisTemplate.opsForList().range(key, 0, -1);
        Optional<ApplicationInstance> op =
                instances.stream().filter(i -> i.equals(instance)).findFirst();
        if (op.isPresent()) {
            ApplicationInstance existedInstance = op.get();
            if (existedInstance.getMetadata() != null) {
                existedInstance.getMetadata().putAll(metadata);
            } else {
                existedInstance.setMetadata(metadata);
            }
            int index = instances.indexOf(existedInstance);
            if (index != -1) {
                redisTemplate.opsForList().set(key, index, existedInstance);
                if (log.isInfoEnabled()) {
                    log.info("Update metadata of application instance '{}'", existedInstance);
                }
            }
        }
    }

    @Override
    public Map<String, String> getMetadata(ServiceInstance instance) {
        String key = getKey(instance.getServiceId());
        List<ApplicationInstance> instances = redisTemplate.opsForList().range(key, 0, -1);
        Optional<ApplicationInstance> op =
                instances.stream().filter(i -> i.equals(instance)).findFirst();
        if (op.isPresent()) {
            ApplicationInstance existedInstance = op.get();
            return existedInstance.getMetadata();
        }
        return Collections.emptyMap();
    }

    @Override
    public void updateInstance(ServiceInstance instance) {
        String key = getKey(instance.getServiceId());
        List<ApplicationInstance> instances = redisTemplate.opsForList().range(key, 0, -1);
        Optional<ApplicationInstance> op =
                instances.stream().filter(i -> i.equals(instance)).findFirst();
        if (op.isPresent()) {
            ApplicationInstance existed = op.get();
            BeanUtils.copyProperties(instance, existed);
            int index = instances.indexOf(existed);
            if (index != -1) {
                redisTemplate.opsForList().set(key, index, existed);
                if (log.isInfoEnabled()) {
                    log.info("Update application instance '{}'", existed);
                }
            }
        }
    }

    @Override
    public void cleanExpiredInstances(ServiceInstance instance) {
        String key = getKey(instance.getServiceId());
        List<ApplicationInstance> applicationInstances =
                redisTemplate.opsForList().range(key, 0, -1);
        applicationInstances.stream()
                .filter(info -> siblingApplicationCondition
                        .isSiblingApplication((ApplicationInstance) info, instance))
                .forEach(info -> {
                    if (info.equals(instance) || !ping.isAlive(info)) {
                        deregisterInstance(info);
                        if (log.isInfoEnabled()) {
                            log.info("Remove expired application instance '{}'", info);
                        }
                    }
                });
    }

    @Override
    public void setInstanceStatus(ServiceInstance instance, String status) {
        Set<String> results = MapUtils.getOrCreate(maintainanceMap, instance.getServiceId(),
                () -> new HashSet<>());
        switch (status) {
            case UP: {
                results.add(instance.getInstanceId());
                break;
            }
            case DOWN: {
                results.remove(instance.getInstanceId());
                break;
            }
            default:
                throw new UnsupportedOperationException("Unexpected value: " + status);
        }

    }

    @Override
    public String getInstanceStatus(ServiceInstance instance) {
        Set<String> results = keepAliveMap.get(instance.getServiceId());
        return results != null && results.contains(instance.getInstanceId()) ? UP : DOWN;
    }

    static String getKey(String applicationName) {
        return String.format(SERVICE_INSTANCE_KEY_PREFIX, applicationName);
    }

    @Override
    public boolean change() throws Exception {
        Collection<String> serviceNames = getServiceNames();
        for (String serviceName : serviceNames) {
            String key = getKey(serviceName);
            List<ApplicationInstance> applicationInstances =
                    redisTemplate.opsForList().range(key, 0, -1);
            for (ApplicationInstance applicationInstance : applicationInstances) {
                Set<String> results =
                        MapUtils.getOrCreate(keepAliveMap, serviceName, () -> new HashSet<>());
                if (applicationInstance.equals(this.selfInstance)) {
                    results.add(applicationInstance.getInstanceId());
                    continue;
                }
                if (ping.isAlive(applicationInstance) && !isMaintainable(applicationInstance)) {
                    if (!results.contains(applicationInstance.getInstanceId())) {
                        results.add(applicationInstance.getInstanceId());
                        applicationEventPublisher.publishEvent(new InstanceStatusChangeEvent(
                                applicationInstance, InstanceStatus.UP));
                    }
                } else {
                    if (results.remove(applicationInstance.getInstanceId())) {
                        if (log.isWarnEnabled()) {
                            log.warn("Detached application instance '{}'", applicationInstance);
                        }
                        applicationEventPublisher.publishEvent(new InstanceStatusChangeEvent(
                                applicationInstance, InstanceStatus.DOWN));
                    }
                }
            }
        }
        return true;
    }

    protected boolean isMaintainable(ApplicationInstance applicationInstance) {
        Set<String> results = maintainanceMap.get(applicationInstance.getServiceId());
        return results != null && results.contains(applicationInstance.getInstanceId());
    }



}
