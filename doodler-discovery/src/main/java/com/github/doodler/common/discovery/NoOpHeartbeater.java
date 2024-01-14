package com.github.doodler.common.discovery;

/**
 * @Description: NoOpHeartbeater
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
public class NoOpHeartbeater implements Heartbeater {

	@Override
	public boolean isAlive(ApplicationInfo info) {
		return true;
	}
}