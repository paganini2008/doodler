package com.github.doodler.common.feign;

/**
 * 
 * @Description: ServiceInstanceFactoryBean
 * @Author: Fred Feng
 * @Date: 05/04/2023
 * @Version 1.0.0
 */
public interface ServiceInstanceFactoryBean<API> {
	
	String getServiceId();
	
	Class<API> getApiInterfaceClass();
	
	default String[] getUrls() {
		return new String[0];
	}

	ServiceInstanceSupplier<API> getSupplier();

}