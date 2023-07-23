package io.doodler.discovery;

/**
 * @Description: NoOpPingStrategy
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
public class NoOpPingStrategy implements PingStrategy {

	@Override
	public boolean isAlive(ApplicationInfo info) {
		return true;
	}
}