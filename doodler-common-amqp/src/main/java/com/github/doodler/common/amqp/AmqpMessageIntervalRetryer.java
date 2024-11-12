package com.github.doodler.common.amqp;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import com.github.doodler.common.amqp.RetryCache.CachedObject;

/**
 * @Description: AmqpMessageIntervalRetryer
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class AmqpMessageIntervalRetryer implements Runnable, InitializingBean, DisposableBean {

    private final RetryCache retryCache;
    private final TaskScheduler taskScheduler;
    private final AmqpMessageSender amqpMessageSender;
    private ScheduledFuture<?> future;

    @Override
    public void afterPropertiesSet() {
        future = taskScheduler.scheduleWithFixedDelay(this, Duration.ofSeconds(5));
    }

    @Override
    public void run() {
        if (retryCache.isEmpty()) {
            return;
        }
        retryCache.remainingIds().forEach(id -> {
            CachedObject cachedObject = retryCache.getObject(id);
            if (cachedObject.getRetryAt() < 0 || cachedObject.getRetryAt() < System.currentTimeMillis()) {
                cachedObject = retryCache.removeObject(id);
                if (log.isDebugEnabled()) {
                    log.debug(cachedObject.toString());
                }
                if (cachedObject.getObject() instanceof Message) {
                    amqpMessageSender.send(cachedObject.getExchange(), cachedObject.getRoutingKey(),
                            (Message) cachedObject.getObject());
                } else {
                    amqpMessageSender.convertAndSend(cachedObject.getExchange(), cachedObject.getRoutingKey(),
                            cachedObject.getObject());
                }
            }
        });
    }

    @Override
    public void destroy() {
        if (future != null) {
            future.cancel(true);
        }
    }
}