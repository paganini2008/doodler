package com.github.doodler.common.feign;

import org.apache.commons.lang3.reflect.ConstructorUtils;

/**
 * @Description: DefaultLoadBalancerFactory
 * @Author: Fred Feng
 * @Date: 27/01/2020
 * @Version 1.0.0
 */
public class DefaultLoadBalancerFactory implements LoadBalancerFactory {

	@Override
	public LoadBalancer createLoadBalancer(String lbName, Class<?> lbType) {
		try {
			return (LoadBalancer) ConstructorUtils.invokeConstructor(lbType);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}