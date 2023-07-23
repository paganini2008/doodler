package io.doodler.cluster;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.doodler.common.context.InstanceId;
import io.doodler.common.utils.NetUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ServiceInstanceHolder
 * @Author: Fred Feng
 * @Date: 18/07/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
@Getter
public class ServiceInstanceHolder {

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Value("${server.port}")
	private int port;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.weight:1}")
	private int weight;

	@Value("${spring.mvc.servlet.path:}")
	private String contextPath;

	private final String localHost = NetUtils.getLocalHost();

	private final String publicIp = NetUtils.getPublicIp();

	@Autowired
	private InstanceId instanceId;

	private ServiceInstance thisInstance;

	private ServiceInstance leaderInstance;

	public void setLeaderInstance(ServiceInstance leaderInstance) {
		if(leaderInstance!=null) {
			if (this.leaderInstance == null || !this.leaderInstance.equals(leaderInstance)) {
				if (log.isInfoEnabled()) {
					log.info("New leader instance: {}",leaderInstance.toString());
				}
			}
		}else {
			if (log.isWarnEnabled()) {
				log.warn("No leader instance is available now");
			}
		}
		this.leaderInstance = leaderInstance;
	}

	public boolean isLeader() {
		return isLeader(thisInstance);
	}

	public boolean isLeader(ServiceInstance serviceInstance) {
		return leaderInstance != null ? leaderInstance.equals(serviceInstance) : false;
	}

	@PostConstruct
	public void initialize() {
		ServiceInstance serviceInstance = new ServiceInstance(clusterName, instanceId.get());
		serviceInstance.setApplicationName(applicationName);
		serviceInstance.setContextPath(contextPath);
		serviceInstance.setWeight(weight);
		serviceInstance.setPort(port);
		serviceInstance.setHost(localHost);
		serviceInstance.setPublicIp(publicIp);
		serviceInstance.setSecure(false);
		this.thisInstance = serviceInstance;
	}
}