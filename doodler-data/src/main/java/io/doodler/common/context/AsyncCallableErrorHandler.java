package io.doodler.common.context;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.ErrorHandler;

/**
 * @Description: AsyncCallableErrorHandler
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class AsyncCallableErrorHandler implements ErrorHandler, ApplicationEventPublisherAware {

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void handleError(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        applicationEventPublisher.publishEvent(new ErrorApplicationEvent(this, e));
    }
}