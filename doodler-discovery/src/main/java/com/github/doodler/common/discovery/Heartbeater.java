package com.github.doodler.common.discovery;

/**
 * @Description: Heartbeater
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface Heartbeater {

	boolean isAlive(ApplicationInfo info);
}