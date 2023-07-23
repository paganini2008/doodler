package io.doodler.cluster;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.doodler.common.context.ManagedBeanLifeCycle;
import io.doodler.common.utils.ExecutorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: HeartBeater
 * @Author: Fred Feng
 * @Date: 21/07/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HeartBeater implements Runnable, ManagedBeanLifeCycle {

	private final ServiceInstance serviceInstance;

	private final PingStrategy pingStrategy;

	private final ServiceInstanceRegistrar serviceInstanceRegistrar;

	private ScheduledExecutorService executor;

	private boolean autoClosed;

	public void setExecutor(ScheduledExecutorService executor) {
		if (executor != null) {
			this.executor = executor;
			this.autoClosed = false;
		}
	}

	public ScheduledFuture<?> start() {
		if (log.isInfoEnabled()) {
			log.info("Start to keep heartbeat with instance '{}:{}'", serviceInstance.getHost(), serviceInstance.getPort());
		}
		return executor.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.executor == null) {
			this.executor = Executors.newSingleThreadScheduledExecutor();
			this.autoClosed = true;
		}
	}

	@Override
	public void destroy() throws Exception {
		if (autoClosed && executor != null) {
			ExecutorUtils.gracefulShutdown(executor, 60000L);
		}
	}

	@Override
	public void run() {
		if (pingStrategy.isAlive(serviceInstance) == false) {
			serviceInstanceRegistrar.removeInstance(serviceInstance);
		}
	}
}