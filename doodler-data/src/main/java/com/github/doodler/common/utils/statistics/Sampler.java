package com.github.doodler.common.utils.statistics;

/**
 * 
 * @Description: Sampler
 * @Author: Fred Feng
 * @Date: 25/09/2023
 * @Version 1.0.0
 */
public interface Sampler<T> {

	 long getTimestamp();
	 
	 T getSample();
	
}
