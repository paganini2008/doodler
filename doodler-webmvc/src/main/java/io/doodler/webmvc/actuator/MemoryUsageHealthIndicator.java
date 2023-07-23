package io.doodler.webmvc.actuator;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

/**
 * @Description: MemoryUsageHealthIndicator
 * @Author: Fred Feng
 * @Date: 25/01/2023
 * @Version 1.0.0
 */
@Component
public class MemoryUsageHealthIndicator extends AbstractHealthIndicator {

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		String repr = FileUtils.byteCountToDisplaySize(totalMemory - freeMemory) + "/" +
				FileUtils.byteCountToDisplaySize(totalMemory);
		builder.up().withDetail("memoryUsed", repr);
	}
}