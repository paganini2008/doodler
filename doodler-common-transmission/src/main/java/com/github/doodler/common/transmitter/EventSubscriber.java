package com.github.doodler.common.transmitter;

/**
 * 
 * @Description: EventSubscriber
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
public interface EventSubscriber<E> {

    void consume(E event);

    default void handleError(Throwable e) {}

}
