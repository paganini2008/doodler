package com.github.doodler.common.amqp;

import java.util.UUID;

import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @Description: FixedRabbitTemplate
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
public class FixedRabbitTemplate extends RabbitTemplate {

	@Override
	protected Message convertMessageIfNecessary(Object object) {
		if (object instanceof Message) {
			return (Message) object;
		}
		MessageConverter converter = getMessageConverter();
		if (converter == null) {
			throw new AmqpIllegalStateException(
					"No 'messageConverter' specified. Check configuration of RabbitTemplate.");
		}
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader("guid", UUID.randomUUID().toString());
		return converter.toMessage(object, messageProperties);
	}
}