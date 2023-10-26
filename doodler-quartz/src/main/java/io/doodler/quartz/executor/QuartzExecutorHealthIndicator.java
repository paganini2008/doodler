package io.doodler.quartz.executor;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

import io.doodler.discovery.ApplicationInfo;
import io.doodler.discovery.DiscoveryClientService;
import io.doodler.quartz.JobConstants;
import lombok.RequiredArgsConstructor;

/**
 * @Description: QuartzExecutorHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class QuartzExecutorHealthIndicator extends AbstractHealthIndicator {

	private final DiscoveryClientService discoveryClientService;

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		Collection<ApplicationInfo> applicationInfos = discoveryClientService.getApplicationInfos(
				JobConstants.DEFAULT_JOB_SERVICE_NAME);
		if (CollectionUtils.isEmpty(applicationInfos)) {
			builder.status(new Status("JOB_SERVER_UNAVAILABLE", "Job Server is not unavailable"));
		} else {
			builder.up();
		}
		builder.build();
	}
}