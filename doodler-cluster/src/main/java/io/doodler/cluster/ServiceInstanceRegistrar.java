package io.doodler.cluster;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hazelcast.collection.IList;
import com.hazelcast.collection.ItemEvent;
import com.hazelcast.collection.ItemListener;
import com.hazelcast.core.HazelcastInstance;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ApplicationClusterInitializer
 * @Author: Fred Feng
 * @Date: 18/07/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class ServiceInstanceRegistrar implements ItemListener<ServiceInstance>, ApplicationEventPublisherAware {

	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	@Autowired
	private PingStrategy pingStrategy;

	@Autowired
	private ServiceInstanceHolder serviceInstanceHolder;

	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	@Value("${spring.application.name}")
	private String applicationName;

	public List<ServiceInstance> getInstances() {
		return hazelcastInstance.getList("instances");
	}

	public List<ServiceInstance> getInstances(String applicationName) {
		return hazelcastInstance.getList(String.format("instance:%s", applicationName));
	}

	public void addInstance(ServiceInstance serviceInstance) {
		IList<ServiceInstance> allInstances = hazelcastInstance.getList("instances");
		if (!allInstances.contains(serviceInstance)) {
			allInstances.add(serviceInstance);
		}
		IList<ServiceInstance> instances = hazelcastInstance
				.getList(String.format("instance:%s", serviceInstance.getApplicationName()));
		if (!instances.contains(serviceInstance)) {
			instances.add(serviceInstance);
		}
	}

	public void removeInstance(ServiceInstance serviceInstance) {
		IList<ServiceInstance> allInstances = hazelcastInstance.getList("instances");
		allInstances.remove(serviceInstance);
		IList<ServiceInstance> instances = hazelcastInstance
				.getList(String.format("instance:%s", serviceInstance.getApplicationName()));
		instances.remove(serviceInstance);

	}

	@EventListener({ ApplicationReadyEvent.class })
	public void onContextRefreshedEvent() {
		ServiceInstance serviceInstance = serviceInstanceHolder.getThisInstance();
		IList<ServiceInstance> allInstances = hazelcastInstance.getList("instances");
		allInstances.addItemListener(this, true);
		allInstances.add(serviceInstance);

		IList<ServiceInstance> instances = hazelcastInstance
				.getList(String.format("instance:%s", applicationName));
		instances.add(serviceInstance);
	}

	@Override
	public void itemAdded(ItemEvent<ServiceInstance> item) {
		ServiceInstance serviceInstance = item.getItem();
		if (log.isInfoEnabled()) {
			log.info("ServiceInstance added: {}", serviceInstance);
		}
		if(!serviceInstanceHolder.getThisInstance().equals(serviceInstance)) {
			HeartBeater heartBeater = new HeartBeater(serviceInstance, pingStrategy, this);
			heartBeater.start();
		}
		applicationEventPublisher.publishEvent(new ServiceInstanceLifeCycleEvent(this, serviceInstance, LifeCycleState.ONLINE));
	}

	@Override
	public void itemRemoved(ItemEvent<ServiceInstance> item) {
		ServiceInstance serviceInstance = item.getItem();
		if (log.isInfoEnabled()) {
			log.info("ServiceInstance removed: {}", serviceInstance);
		}
		applicationEventPublisher.publishEvent(new ServiceInstanceLifeCycleEvent(this, serviceInstance, LifeCycleState.OFFLINE));
	}




	
	
}