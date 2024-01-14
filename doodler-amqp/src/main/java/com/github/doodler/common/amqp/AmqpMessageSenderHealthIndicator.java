package com.github.doodler.common.amqp;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import lombok.RequiredArgsConstructor;

/**
 * @Description: AmqpMessageSenderHealthIndicator
 * @Author: Fred Feng
 * @Date: 11/12/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class AmqpMessageSenderHealthIndicator extends AbstractHealthIndicator {

	private final AmqpMessageStatistics amqpMessageStatistics;

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up();
		builder.withDetail("sent", amqpMessageStatistics.getPushCount())
				.withDetail("received", amqpMessageStatistics.getPullCount())
				.withDetail("errors", amqpMessageStatistics.getErrorCount());
		builder.build();
	}
}