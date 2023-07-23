package io.doodler.amqp.eventbus;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;

import io.doodler.amqp.AmqpConsumerProperties;
import io.doodler.amqp.MessageHistoryCollector;
import io.doodler.common.utils.Markers;


/**
 * @Description: EventBusConfig
 * @Author: Fred Feng
 * @Date: 13/01/2023
 * @Version 1.0.0
 */
@ConditionalOnProperty("spring.rabbitmq.consumer.eventbus.enabled")
@Configuration(proxyBeanMethods = false)
public class EventBusConfig {
	
	@Value("${spring.application.name}")
	private String applicationName;

    @ConditionalOnMissingBean
    @Bean
    public MessageConverter defaultMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public ClassMapper classMapper() {
        return new ClassMapper();
    }

    @Bean("amqp-eventbus")
    public EventBus eventBus() {
        return new EventBus("amqp-eventbus");
    }

    @Bean
    public EventBusListenerBeanProcessor eventBusListenerBeanProcessor(EventBus eventBus, ClassMapper classMapper) {
        return new EventBusListenerBeanProcessor(eventBus, classMapper);
    }

    @Bean
    public EventBusAspect eventBusAspect() {
        return new EventBusAspect();
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(AmqpConsumerProperties config,
                                                                   ConnectionFactory connectionFactory,
                                                                   TaskExecutor taskExecutor,
                                                                   EventBus eventBus,
                                                                   Jackson2JsonMessageConverter messageConverter,
                                                                   ClassMapper classMapper,
                                                                   MessageHistoryCollector messageHistoryCollector) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        if (CollectionUtils.isNotEmpty(config.getQueues())) {
            container.setQueueNames(config.getQueues().toArray(new String[0]));
        }
        container.setTaskExecutor(taskExecutor);
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMaxConcurrentConsumers(20);
        container.setPrefetchCount(100);
        container.setMessageListener(new EventBusAmqpMessageListener(eventBus, messageConverter, classMapper, messageHistoryCollector, Markers.forName(applicationName)));
        return container;
    }
}