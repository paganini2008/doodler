package io.doodler.cluster;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: ClusterLeaderRecoveryListener
 * @Author: Fred Feng
 * @Date: 23/07/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class LeaderRecoveryListener implements ApplicationEventPublisherAware {

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Autowired
	private ServiceInstanceHolder serviceInstanceHolder;

	@Autowired
	private ServiceInstanceRegistrar serviceInstanceRegistrar;

	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	@EventListener(LeaderRecoveryEvent.class)
	public void onLeaderRecoveryEvent(LeaderRecoveryEvent event) {
		if (log.isWarnEnabled()) {
			log.warn("Lost leader instance: {}", event.getLostLeaderInstance().toString());
		}

		List<ServiceInstance> serviceInstances = serviceInstanceRegistrar.getInstances();
		serviceInstances.sort((left, right) -> {
			return Long.compare(left.getUptime(), right.getUptime());
		});
		ServiceInstance serviceInstance = serviceInstances.get(0);
		serviceInstanceHolder.setLeaderInstance(serviceInstance);

		applicationEventPublisher
				.publishEvent(new LeaderElectionEvent(this, serviceInstance, serviceInstanceHolder.isLeader()));
	}

}
