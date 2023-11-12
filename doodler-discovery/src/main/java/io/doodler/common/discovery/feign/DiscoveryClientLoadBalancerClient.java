package io.doodler.common.discovery.feign;

import feign.Request;
import io.doodler.common.discovery.ApplicationInfo;
import io.doodler.common.discovery.DiscoveryClientChangeEvent;
import io.doodler.common.discovery.DiscoveryClientService;
import io.doodler.common.discovery.AffectedApplicationInfo.AffectedType;
import io.doodler.common.feign.LoadBalancer;
import io.doodler.common.feign.LoadBalancerClient;
import io.doodler.common.feign.RobinLoadBalancer;
import io.doodler.common.feign.ServiceInstance;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

/**
 * @Description: DiscoveryClientLoadBalancerClient
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DiscoveryClientLoadBalancerClient
        implements LoadBalancerClient, InitializingBean, ApplicationListener<DiscoveryClientChangeEvent> {

    private final Map<String, List<ServiceInstance>> candidates = new ConcurrentHashMap<>();

    private final DiscoveryClientService discoveryClientService;

    private final boolean usePublicIp;

    private final Map<String, ServiceInstance> currentSelected = new ConcurrentHashMap<>();

    private LoadBalancer defaultLoadBalancer = new RobinLoadBalancer();

    public void setDefaultLoadBalancer(LoadBalancer loadBalancer) {
        this.defaultLoadBalancer = loadBalancer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refresh();
    }

    public boolean refresh() {
        Map<String, Collection<ApplicationInfo>> results = discoveryClientService.getApplicationInfos();
        for (Map.Entry<String, Collection<ApplicationInfo>> entry : results.entrySet()) {
            candidates.put(entry.getKey(), createServiceInstance(entry.getValue()));
        }
        if (log.isInfoEnabled()) {
            if (candidates.isEmpty()) {
                log.warn("LoadBalancerClient has no instances available to route. Please check again.");
            } else {
                candidates.entrySet().forEach(e -> {
                    log.info("LoadBalancerClient has loaded {} instance(s) of application: {}", e.getValue().size(),
                            e.getKey());
                });
            }
        }
        return candidates.size() > 0;
    }

    private List<ServiceInstance> createServiceInstance(Collection<ApplicationInfo> infos) {
        List<ServiceInstance> list = new ArrayList<>();
        for (ApplicationInfo info : infos) {
            for (int i = 0; i < info.getWeight(); i++) {
                ServiceInstance serviceInstance = new ServiceInstance();
                serviceInstance.setServiceId(info.getApplicationName());
                serviceInstance.setUrl(info.toHostUrl(usePublicIp));
                serviceInstance.setContextPath(info.getContextPath());
                serviceInstance.setOnline(true);
                serviceInstance.setServiceDetail(info);
                list.add(serviceInstance);
            }
        }
        return list;
    }

    @Override
    public Map<String, Collection<ServiceInstance>> candidates() {
        return candidates.entrySet().stream().collect(HashMap::new,
                (m, e) -> m.put(e.getKey(), new LinkedHashSet<>(e.getValue())),
                HashMap::putAll);
    }

    @Override
    public boolean contains(String serviceId) {
        return candidates.containsKey(serviceId);
    }

    @Override
    public ServiceInstance chooseFirst(String serviceId) {
        List<ServiceInstance> serviceInstances = candidates.get(serviceId);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        if (serviceInstances.stream().anyMatch(i -> !i.isOnline())) {
            serviceInstances = serviceInstances.stream().filter(i -> i.isOnline()).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        ServiceInstance selectedInstance = serviceInstances.get(0);
        currentSelected.put(serviceId, selectedInstance);
        return selectedInstance;
    }

    @Override
    public ServiceInstance choose(String serviceId, Request request) {
        List<ServiceInstance> serviceInstances = candidates.get(serviceId);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        if (serviceInstances.stream().anyMatch(i -> !i.isOnline())) {
            serviceInstances = serviceInstances.stream().filter(i -> i.isOnline()).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        ServiceInstance selectedInstance = defaultLoadBalancer.choose(serviceId, serviceInstances, request);
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

    @Override
    public void onApplicationEvent(DiscoveryClientChangeEvent event) {
        if (CollectionUtils.isNotEmpty(event.getAffects())) {
            event.getAffects().stream().filter(e -> e.getAffectedType() == AffectedType.OFFLINE).forEach(e -> {
                candidates.remove(e.getApplicationInfo().getApplicationName());
            });
        }
        boolean updated = refresh();
        if (updated) {
            if (log.isInfoEnabled()) {
                log.info("LoadBalancerClient update candidate list successfully");
            }
        }
    }
}