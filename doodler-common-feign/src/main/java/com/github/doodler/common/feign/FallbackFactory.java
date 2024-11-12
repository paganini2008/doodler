package com.github.doodler.common.feign;

/**
 * 
 * @Description: FallbackFactory
 * @Author: Fred Feng
 * @Date: 02/02/2023
 * @Version 1.0.0
 */
public interface FallbackFactory<API>{
	
	API createFallback(Throwable e);
	
}
