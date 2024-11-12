package com.github.doodler.common.quartz.executor;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import com.github.doodler.common.quartz.scheduler.JobBeanNotFoundException;

/**
 * @Description: JobBeanFactory
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
public class DefaultJobBeanFactory implements ApplicationContextAware, JobBeanFactory {

	private ApplicationContext ctx;

	private AutowireCapableBeanFactory beanFactory;

	public Object getBean(String className) {
		Class<?> jobClass;
		try {
			jobClass = ClassUtils.forName(className, Thread.currentThread().getContextClassLoader());
		} catch (Exception e) {
			throw new JobBeanNotFoundException(className);
		}
		try {
			return ctx.getBean(jobClass);
		} catch (BeansException e) {
			Object targetBean;
			try {
				targetBean = ConstructorUtils.invokeConstructor(jobClass);
			} catch (Exception ignored) {
				throw new JobBeanNotFoundException(jobClass.getName());
			}
			beanFactory.autowireBean(targetBean);
			return targetBean;
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
		this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}
}