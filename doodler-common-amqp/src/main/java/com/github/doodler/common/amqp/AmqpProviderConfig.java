package com.github.doodler.common.amqp;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Description: AmqpProviderConfig
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class AmqpProviderConfig {

    @ConditionalOnMissingBean
    @Bean
    public MessageConverter defaultMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public AmqpProviderStateCallback amqpProviderStateCallback(RetryCache retryCache) {
        return new CheckedAmqpProviderStateCallback(retryCache);
    }

    @Primary
    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter,
                                       AmqpProviderStateCallback amqpProviderStateCallback) {
        RabbitTemplate rabbitTemplate = new FixedRabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(amqpProviderStateCallback);
        rabbitTemplate.setReturnsCallback(amqpProviderStateCallback);
        return rabbitTemplate;
    }

    @Bean
    public AmqpMessageSender amqpMessageSender(AmqpAdminService amqpAdminService,
                                               RabbitTemplate rabbitTemplate,
                                               MessagePostProcessor messagePostProcessor,
                                               RetryCache retryCache,
                                               AmqpMessageStatistics messageHistoryCollector) {
        return new AmqpMessageSender(amqpAdminService, rabbitTemplate, messagePostProcessor, retryCache, messageHistoryCollector);
    }

    @ConditionalOnMissingBean
    @Bean
    public MessagePostProcessor messagePostProcessor() {
        return new DefaultMessagePostProcessor();
    }

    @ConditionalOnMissingBean
    @Bean
    public RetryCache retryCache() {
        return new LruRetryCache();
    }

    @Bean
    public AmqpMessageIntervalRetryer amqpMessageIntervalRetryer(RetryCache retryCache, TaskScheduler taskScheduler,
                                                                 AmqpMessageSender amqpMessageSender) {
        return new AmqpMessageIntervalRetryer(retryCache, taskScheduler, amqpMessageSender);
    }
}