package com.github.doodler.common.quartz.executor;

/**
 * @Description: JobBeanFactory
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
public interface JobBeanFactory {

    Object getBean(String className);
}