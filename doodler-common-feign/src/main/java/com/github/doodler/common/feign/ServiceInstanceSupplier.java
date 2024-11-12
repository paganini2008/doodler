package com.github.doodler.common.feign;

/**
 * @Description: ServiceInstanceSupplier
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface ServiceInstanceSupplier<API> {

	API getServiceInstance(String url);
}