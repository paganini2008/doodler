package com.github.doodler.common.amqp;

import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_DEADLETTER_EXCHANGE_NAME;
import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_DEADLETTER_QUEUE_NAME;
import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_DEADLETTER_ROUTING_KEY_NAME;
import static com.github.doodler.common.amqp.AmqpConstants.DEFAULT_FANOUT_EXCHANGE_NAME;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: AmqpConsumerConfig
 * @Author: Fred Feng
 * @Date: 12/01/2023
 * @Version 1.0.0
 */
@EnableRabbit
@EnableConfigurationProperties({AmqpConsumerProperties.class})
@Configuration(proxyBeanMethods = false)
public class AmqpConsumerConfig {

	@ConditionalOnExpression("#{'${spring.rabbitmq.consumer.eventbus.enabled:false}' == 'false'}")
    @ConditionalOnProperty(name = "spring.rabbitmq.consumer.ack", havingValue = "1")
    @Bean("manualAckContainerFactory")
    public SimpleRabbitListenerContainerFactory manualAckContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        containerFactory.setMaxConcurrentConsumers(20);
        containerFactory.setPrefetchCount(100);
        return containerFactory;
    } 
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEFAULT_DEADLETTER_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEFAULT_DEADLETTER_QUEUE_NAME, true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DEFAULT_DEADLETTER_ROUTING_KEY_NAME);
    }
    
    @Bean
    public FanoutExchange defaultExchange() {
        return new FanoutExchange(DEFAULT_FANOUT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue defaultQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEFAULT_DEADLETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DEFAULT_DEADLETTER_ROUTING_KEY_NAME);
        //args.put("x-message-ttl",new Long(60000));
        ApplicationQueue applicationQueue =  ApplicationQueue.forName(applicationName);
        return new Queue(applicationQueue.getRepr(), true, false, false, args);
    }

    @Bean
    public Binding defaultBinding() {
        return BindingBuilder.bind(defaultQueue()).to(defaultExchange());
    }
}