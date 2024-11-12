package com.github.doodler.common.quartz.scheduler;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * @Description: JobBeanNotFoundException
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public class JobBeanNotFoundException extends NoSuchBeanDefinitionException {

	private static final long serialVersionUID = -6286994326425476179L;

	public JobBeanNotFoundException(String msg) {
		super(msg);
	}
}