package com.github.doodler.common.validation;

import java.beans.Introspector;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import com.github.doodler.common.context.GenericBeanDefinitionRegistrarSupport;

/**
 * @Description: ErrorCodeFinderRegistrar
 * @Author: Fred Feng
 * @Date: 07/01/2023
 * @Version 1.0.0
 */
public class ErrorCodeFinderRegistrar extends GenericBeanDefinitionRegistrarSupport {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		final String packageName = ClassUtils.getPackageName(annotationMetadata.getClassName());
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ErrorCodeFinder.class);
		builder.addConstructorArgValue(packageName);
		builder.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
		BeanDefinition beanDefinition = builder.getRawBeanDefinition();
		String possibleBeanName = Introspector.decapitalize(ErrorCodeFinder.class.getSimpleName());
		registry.registerBeanDefinition(possibleBeanName, beanDefinition);
	}
}