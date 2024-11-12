package com.github.doodler.common.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import lombok.Setter;

/**
 * @Description: GenericBeanDefinitionRegistrarSupport
 * @Author: Fred Feng
 * @Date: 03/01/2023
 * @Version 1.0.0
 */
public abstract class GenericBeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar, BeanFactoryAware,
        BeanClassLoaderAware, ResourceLoaderAware, EnvironmentAware {

	protected final Log log = LogFactory.getLog(getClass());
	
    @Setter
    protected BeanFactory beanFactory;

    @Setter
    protected ClassLoader beanClassLoader;

    @Setter
    protected ResourceLoader resourceLoader;

    @Setter
    protected Environment environment;
}