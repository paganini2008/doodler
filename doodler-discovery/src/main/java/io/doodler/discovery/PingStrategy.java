package io.doodler.discovery;

/**
 * @Description: PingStrategy
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface PingStrategy {

	boolean isAlive(ApplicationInfo info);
}