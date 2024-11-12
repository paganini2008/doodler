package com.github.doodler.common.amqp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @Description: CheckedAmqpProviderStateCallback
 * @Author: Fred Feng
 * @Date: 15/01/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CheckedAmqpProviderStateCallback implements AmqpProviderStateCallback {

    private final RetryCache retryCache;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String reason) {
        if (ack) {
            if (correlationData != null) {
                Object payload = retryCache.removeObject(correlationData.getId());
                if (payload != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Remove message data from cache successfully and current size of cache is {}",
                                retryCache.size());
                    }
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("CorrelationData is not nullable.");
                }
            }
        } else {
            if (StringUtils.isNotBlank(reason)) {
                if (log.isErrorEnabled()) {
                    log.error("An exception from the provider side: {}", reason);
                }
            }
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        if (log.isTraceEnabled()) {
            log.trace("{}", returned);
        }
    }
}