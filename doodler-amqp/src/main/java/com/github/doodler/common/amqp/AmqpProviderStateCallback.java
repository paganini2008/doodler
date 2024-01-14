package com.github.doodler.common.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Description: AmqpProviderStateCallback
 * @Author: Fred Feng
 * @Date: 15/03/2021
 * @Version 1.0.0
 */
public interface AmqpProviderStateCallback extends RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

}