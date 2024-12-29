package com.github.doodler.common.transmitter;

import java.util.Collection;

/**
 * 
 * @Description: EventPublisher
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
public interface EventPublisher<T> {

    int subscribe(Collection<EventSubscriber<T>> subscribers);

    void publish(T event);

    default void destroy() {}

}
