package io.doodler.common.feign.statistics;

/**
 * @Description: TpsUpdater
 * @Author: Fred Feng
 * @Date: 22/09/2023
 * @Version 1.0.0
 */
public interface TpsUpdater {

	void incr();

	int get();

	void set();
}