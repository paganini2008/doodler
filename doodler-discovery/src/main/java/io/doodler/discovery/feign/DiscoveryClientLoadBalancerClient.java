package io.doodler.discovery.feign;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import feign.Request;
import io.doodler.discovery.ApplicationInfo;
import io.doodler.discovery.DiscoveryClientChangeEvent;
import io.doodler.discovery.DiscoveryClientService;
import io.doodler.discovery.AffectedApplicationInfo.AffectedType;
import io.doodler.feign.LoadBalancer;
import io.doodler.feign.LoadBalancerClient;
import io.doodler.feign.RobinLoadBalancer;
import io.doodler.feign.ServiceInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	private LoadBalancer defaultLoadBalancer = new RobinLoadBalancer();

	public void setDefaultLoadBalancer(LoadBalancer loadBalancer) {
		this.defaultLoadBalancer = loadBalancer;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		refresh();
	}

	public boolean refresh() {
		Map<String, Set<ApplicationInfo>> results = discoveryClientService.getApplicationInfos();
		for (Map.Entry<String, Set<ApplicationInfo>> entry : results.entrySet()) {
			candidates.put(entry.getKey(), createServiceInstance(entry.getValue()));
		}
		if (log.isInfoEnabled()) {
			if (candidates.isEmpty()) {
				log.warn("LoadBalancerClient has no instances available to route. Please check again.");
			} else {
				candidates.entrySet().forEach(e -> {
					log.info("LoadBalancerClient has loaded {} instance(s) of application: {}", e.getValue().size(), e.getKey());
				});
			}
		}
		return candidates.size()> 0;
	}

	private List<ServiceInstance> createServiceInstance(Set<ApplicationInfo> infos) {
		List<ServiceInstance> list = new ArrayList<>();
		for (ApplicationInfo info : infos) {
			for (int i = 0; i < info.getWeight(); i++) {
				ServiceInstance serviceInstance = new ServiceInstance();
				serviceInstance.setServiceId(info.getApplicationName());
				serviceInstance.setUrl(info.toHostUrl(usePublicIp));
				serviceInstance.setOnline(true);
				list.add(serviceInstance);
			}
		}
		return list;
	}

	@Override
	public Map<String, Collection<ServiceInstance>> candidates() {
		return candidates.entrySet().stream().collect(HashMap::new, (m, e) -> m.put(e.getKey(), new LinkedHashSet<>(e.getValue())),
				HashMap::putAll);
	}

	@Override
	public boolean contains(String serviceId) {
		return candidates.containsKey(serviceId);
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
		return defaultLoadBalancer.choose(serviceId, serviceInstances, request);
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