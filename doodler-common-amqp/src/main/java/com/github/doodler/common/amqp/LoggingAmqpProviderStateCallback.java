package com.github.doodler.common.amqp;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: LoggingAmqpProviderStateCallback
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class LoggingAmqpProviderStateCallback implements AmqpProviderStateCallback {

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (log.isInfoEnabled()) {
			log.info("{},{},{}", correlationData, ack, cause);
		}
	}

	@Override
	public void returnedMessage(ReturnedMessage returned) {
		if (log.isInfoEnabled()) {
			log.info("{}", returned);
		}
	}
}