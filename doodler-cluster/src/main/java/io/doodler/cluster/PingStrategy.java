package io.doodler.cluster;

/**
 * @Description: PingStrategy
 * @Author: Fred Feng
 * @Date: 22/07/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface PingStrategy {

    boolean isAlive(ServiceInstance serviceInstance);
}