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
 * @Description: LeaderElectionListener
 * @Author: Fred Feng
 * @Date: 22/07/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public final class LeaderElectionListener implements ApplicationEventPublisherAware {

	@Autowired
	private ServiceInstanceHolder serviceInstanceHolder;

	@Autowired
	private ServiceInstanceRegistrar serviceInstanceRegistrar;

	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	@Value("${spring.application.cluster.minimumMembers:2}")
	private int applicationClusterMinimumMembers;

	@EventListener({ServiceInstanceLifeCycleEvent.class})
	public void handleServiceInstanceLifeCycleEvent(ServiceInstanceLifeCycleEvent event) {
		if (event.getState() == LifeCycleState.ONLINE) {
			List<ServiceInstance> serviceInstances = serviceInstanceRegistrar.getInstances();
			if (serviceInstances.size() >= applicationClusterMinimumMembers) {
				serviceInstances.sort((left, right) -> {
					return Long.compare(left.getUptime(), right.getUptime());
				});
				ServiceInstance serviceInstance = serviceInstances.get(0);
				serviceInstanceHolder.setLeaderInstance(serviceInstance);
				applicationEventPublisher
						.publishEvent(new LeaderElectionEvent(this, serviceInstance,serviceInstanceHolder.isLeader()));
			}
		} else if (event.getState() == LifeCycleState.OFFLINE) {
			ServiceInstance lostInstance = event.getServiceInstance();
			if (serviceInstanceHolder.isLeader(lostInstance)) {
				serviceInstanceHolder.setLeaderInstance(null);
				applicationEventPublisher
						.publishEvent(new LeaderRecoveryEvent(this, lostInstance));
			}
		}
	}
	
	@EventListener(LeaderElectionEvent.class)
	public void onClusterLeaderEvent(LeaderElectionEvent event) {
		if (event.isLeader()) {
			if (log.isInfoEnabled()) {
				log.info("This is the leader of application cluster {}", serviceInstanceHolder.getClusterName());
			}
		} else {
			if (log.isInfoEnabled()) {
				log.info("This is a follower of application cluster {}", serviceInstanceHolder.getClusterName());
			}
		}
	}
}