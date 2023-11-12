package io.doodler.common.discovery;

/**
 * @Description: DiscoveryClientConstants
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
public interface DiscoveryClientConstants {

	String REDIS_KEY_PREFIX_DISCOVERY_CLIENT = "service:instance:";

	String REDIS_KEY_PATTERN_DISCOVERY_CLIENT = REDIS_KEY_PREFIX_DISCOVERY_CLIENT + "%s";
}