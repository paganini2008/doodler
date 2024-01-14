package com.github.doodler.common.feign;

import org.springframework.lang.Nullable;

import lombok.Data;

/**
 * @Description: ServiceInstance
 * @Author: Fred Feng
 * @Date: 27/01/2020
 * @Version 1.0.0
 */
@Data
public class ServiceInstance {

	private String serviceId;
	private String url;
	private String contextPath;
	private boolean online;
	
	private @Nullable Object serviceDetail;
}