package com.github.doodler.common.cloud.lb;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import com.github.doodler.common.cloud.ServiceInstance;
import com.github.doodler.common.cloud.lb.LoadBalancerProperties.Instance;
import com.github.doodler.common.utils.CaseInsensitiveMap;
import com.github.doodler.common.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: DefaultLoadBalancerClient
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultLoadBalancerClient implements LoadBalancerClient, InitializingBean {

    private final Map<String, List<ServiceInstance>> candidates =
            new CaseInsensitiveMap<>(new ConcurrentHashMap<>());

    private final Map<String, LoadBalancer> lbs = new ConcurrentHashMap<>();

    private final Map<String, LoadBalancer> appLbs = new ConcurrentHashMap<>();

    private final Map<String, ServiceInstance> currentSelected = new ConcurrentHashMap<>();

    private LoadBalancerProperties config;

    private LoadBalancer defaultLoadBalancer = new RobinLoadBalancer();

    private LoadBalancerFactory loadBalancerFactory = new DefaultLoadBalancerFactory();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (config == null) {
            throw new IllegalStateException("RestClientProperties can not be null");
        }
        for (Instance instance : config.getInstances()) {
            candidates.put(instance.getServiceId(), getServiceInstances(instance));
            LoadBalancer loadBalancer;
            try {
                Class<?> type;
                if (StringUtils.isBlank(instance.getLbType())) {
                    type = RobinLoadBalancer.class;
                } else {
                    type = ClassUtils.forName(instance.getLbType(),
                            Thread.currentThread().getContextClassLoader());
                }
                loadBalancer = MapUtils.getOrCreate(lbs, instance.getLbType(),
                        () -> loadBalancerFactory.createLoadBalancer(null, type));
            } catch (RuntimeException e) {
                loadBalancer = defaultLoadBalancer;
            }
            appLbs.put(instance.getServiceId(), loadBalancer);
        }
    }

    private List<ServiceInstance> getServiceInstances(Instance instance) {
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        int i, weight = 1;
        for (String url : instance.getUrls()) {
            i = url.indexOf("#");
            if (i > 0) {
                weight = Integer.parseInt(url.substring(0, i));
                url = url.substring(i + 1);
            }
            for (int j = 0; j < weight; j++) {
                ServiceInstance serviceInstance = new ServiceInstance();
                serviceInstance.setServiceId(instance.getServiceId());
                serviceInstance.setUrl(url);
                serviceInstance.setOnline(true);
                serviceInstances.add(serviceInstance);
            }
        }
        return serviceInstances;
    }

    @Override
    public boolean contains(String serviceId) {
        return candidates.containsKey(serviceId);
    }

    @Override
    public Map<String, Collection<ServiceInstance>> candidates() {
        return candidates.entrySet().stream().collect(HashMap::new,
                (m, e) -> m.put(e.getKey(), new LinkedHashSet<>(e.getValue())), HashMap::putAll);
    }

    @Override
    public ServiceInstance chooseFirst(String serviceId) {
        List<ServiceInstance> serviceInstances = candidates.get(serviceId);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        if (serviceInstances.stream().anyMatch(i -> !i.isOnline())) {
            serviceInstances = serviceInstances.stream().filter(i -> i.isOnline())
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        ServiceInstance selectedInstance = serviceInstances.get(0);
        currentSelected.put(serviceId, selectedInstance);
        return selectedInstance;
    }

    @Override
    public ServiceInstance choose(String serviceId, Object requestProxy) {
        List<ServiceInstance> serviceInstances = candidates.get(serviceId);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        if (serviceInstances.stream().anyMatch(i -> !i.isOnline())) {
            serviceInstances = serviceInstances.stream().filter(i -> i.isOnline())
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        LoadBalancer loadBalancer = appLbs.get(serviceId);
        ServiceInstance selectedInstance =
                loadBalancer.choose(serviceId, serviceInstances, requestProxy);
        currentSelected.put(serviceId, selectedInstance);
        return selectedInstance;
    }

    @Override
    public ServiceInstance currentSelected(String serviceId) {
        return currentSelected.get(serviceId);
    }

    @Override
    public URI reconstructURI(ServiceInstance instance, URI originalUri) {
        URI uri = URI.create(instance.getUrl());
        String hostUrl = uri.getHost() + ":" + uri.getPort();
        String newUrl = originalUri.toString().replace(instance.getServiceId(), hostUrl);
        return URI.create(newUrl);
    }

    @Override
    public void maintain(String serviceId, String url, boolean online) {
        List<ServiceInstance> serviceInstances = candidates.get(serviceId);
        if (CollectionUtils.isNotEmpty(serviceInstances)) {
            serviceInstances.stream().filter(i -> i.getUrl().equals(url)).forEach(i -> {
                i.setOnline(online);
                if (log.isWarnEnabled()) {
                    log.warn("Set service instance [{}] {} {}", i.getServiceId(), i.getUrl(),
                            online ? "online" : "offline");
                }
            });
        }
    }

    public void setConfig(LoadBalancerProperties config) {
        this.config = config;
    }

    public void setDefaultLoadBalancer(LoadBalancer defaultLoadBalancer) {
        this.defaultLoadBalancer = defaultLoadBalancer;
    }

    public void setLoadBalancerFactory(LoadBalancerFactory loadBalancerFactory) {
        this.loadBalancerFactory = loadBalancerFactory;
    }
}
