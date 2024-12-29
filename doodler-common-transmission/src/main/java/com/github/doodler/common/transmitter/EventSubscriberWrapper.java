package com.github.doodler.common.transmitter;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: EventSubscriberWrapper
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
@Slf4j
public class EventSubscriberWrapper<T> implements Flow.Subscriber<T> {

    private final EventSubscriber<T> subscriber;
    private final int requestFetchSize;

    EventSubscriberWrapper(int requestFetchSize, EventSubscriber<T> subscriber) {
        this.requestFetchSize = requestFetchSize;
        this.subscriber = subscriber;
    }

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(requestFetchSize);
        this.subscription = subscription;
    }

    @Override
    public void onNext(T item) {
        subscriber.consume(item);
        subscription.request(requestFetchSize);
    }

    @Override
    public void onError(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
        subscriber.handleError(e);
    }

    @Override
    public void onComplete() {
        if (log.isInfoEnabled()) {
            log.info("Completed");
        }
    }



}
