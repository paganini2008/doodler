package io.doodler.feign;

import lombok.Data;

/**
 * @Description: ServiceInstance
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Data
public class ServiceInstance {

	private String serviceId;
	private String url;
	private boolean online;
}