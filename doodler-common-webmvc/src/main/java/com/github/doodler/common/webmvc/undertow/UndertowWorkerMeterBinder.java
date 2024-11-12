package com.github.doodler.common.webmvc.undertow;

import java.lang.management.ManagementFactory;
import java.util.Optional;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: UndertowWorkerMeterBinder
 * @Author: Fred Feng
 * @Date: 30/09/2023
 * @Version 1.0.0
 */
@Slf4j
@Component
public class UndertowWorkerMeterBinder implements MeterBinder {

	private static final String OBJECT_NAME = "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\"";
	private static final String GAUGE_NAME_WORKER_QUEUE_SIZE = "undertow.worker.queue.size";
	private static final String GAUGE_NAME_WORKER_POOL_SIZE = "undertow.worker.pool.size";
	private static final String GAUGE_NAME_MAX_WORKER_POOL_SIZE = "undertow.worker.pool.max";
	private static final String GAUGE_NAME_IO_THREAD_COUNT = "undertow.io.thread-count";
	private static final String ATTR_WORKER_QUEUE_SIZE = "WorkerQueueSize";
	private static final String ATTR_WORKER_POOL_SIZE = "CoreWorkerPoolSize";
	private static final String ATTR_MAX_WORKER_POOL_SIZE = "MaxWorkerPoolSize";
	private static final String ATTR_IO_THREAD_COUNT = "IoThreadCount";
	private final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

	@Override
	public void bindTo(@NonNull MeterRegistry registry) {
		buildAndRegisterGauge(GAUGE_NAME_WORKER_QUEUE_SIZE,
				ATTR_WORKER_QUEUE_SIZE,
				"Undertow worker queue size",
				registry);

		buildAndRegisterGauge(GAUGE_NAME_WORKER_POOL_SIZE,
				ATTR_WORKER_POOL_SIZE,
				"Undertow worker pool size",
				registry);

		buildAndRegisterGauge(GAUGE_NAME_MAX_WORKER_POOL_SIZE,
				ATTR_MAX_WORKER_POOL_SIZE,
				"Undertow max worker pool size",
				registry);

		buildAndRegisterGauge(GAUGE_NAME_IO_THREAD_COUNT,
				ATTR_IO_THREAD_COUNT,
				"Undertow IO thread count",
				registry);
	}

	private void buildAndRegisterGauge(@NonNull String name, @NonNull String attributeName, @NonNull String description,
	                                   @NonNull MeterRegistry registry) {
		Gauge.builder(name,
						platformMBeanServer,
						mBeanServer -> getWorkerAttribute(mBeanServer, attributeName))
				.description(description)
				.register(registry);
	}

	private double getWorkerAttribute(@NonNull MBeanServer mBeanServer, @NonNull String attributeName) {
		Object attributeValueObj = null;
		try {
			attributeValueObj = mBeanServer.getAttribute(workerObjectName(), attributeName);
		} catch (Exception e) {
			log.warn("Unable to get {} from JMX", ATTR_WORKER_QUEUE_SIZE, e);
		}
		return Optional.ofNullable(attributeValueObj)
				.map(value -> (Number) value)
				.map(Number::doubleValue)
				.orElse(0d);
	}

	private ObjectName workerObjectName() throws MalformedObjectNameException {
		return new ObjectName(OBJECT_NAME);
	}
}