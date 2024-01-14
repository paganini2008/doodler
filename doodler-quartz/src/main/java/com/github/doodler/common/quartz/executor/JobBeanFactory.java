package com.github.doodler.common.quartz.executor;

/**
 * @Description: JobBeanFactory
 * @Author: Fred Feng
 * @Date: 14/05/2019
 * @Version 1.0.0
 */
public interface JobBeanFactory {

    Object getBean(String className);
}