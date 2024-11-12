package com.github.doodler.common.feign;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import com.github.doodler.common.context.GenericBeanDefinitionRegistrarSupport;

/**
 * @Description: RestClientBeanDefinitionRegistrarSupport
 * @Author: Fred Feng
 * @Date: 31/12/2022
 * @Version 1.0.0
 */
public abstract class RestClientBeanDefinitionRegistrarSupport extends GenericBeanDefinitionRegistrarSupport {

    protected abstract Class<?>[] getApiInterfaceClasses();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String currentApplicationName = environment.getRequiredProperty("spring.application.name");
        for (Class<?> apiInterfaceClass : getApiInterfaceClasses()) {
            String possibleBeanName = Introspector.decapitalize(apiInterfaceClass.getSimpleName());
            if (registry.containsBeanDefinition(apiInterfaceClass.getName()) ||
                    registry.containsBeanDefinition(possibleBeanName) ||
                    registry.containsBeanDefinition(possibleBeanName + "." + RestClientProxyFactoryBean.class.getSimpleName())) {
                continue;
            }
            AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(apiInterfaceClass,
                    RestClient.class);
            if (attributes == null) {
                continue;
            }

            String serviceId = attributes.getString("serviceId");
            if (StringUtils.isNotBlank(serviceId)) {
                if (serviceId.equals(currentApplicationName)) {
                    continue;
                }
            }

            String defaultUrl = attributes.getString("url");
            if (StringUtils.isNotBlank(defaultUrl)) {
            	if(defaultUrl.startsWith("${") && defaultUrl.endsWith("}")) {
            		defaultUrl = ((ConfigurableEnvironment)environment).resolvePlaceholders(defaultUrl);
            	}
            }
            
            Map<String, Object> attr = new HashMap<>(attributes);
            attr.put("serviceId", serviceId);
            attr.put("defaultUrl", defaultUrl);
            
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RestClientProxyFactoryBean.class);
            builder.addConstructorArgValue(serviceId);
            builder.addConstructorArgValue(apiInterfaceClass);
            builder.addConstructorArgValue(defaultUrl);
            builder.addConstructorArgValue(attr);
            builder.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            BeanDefinition beanDefinition = builder.getRawBeanDefinition();
            registry.registerBeanDefinition(possibleBeanName, beanDefinition);

            log.info("Register RestClient: " + possibleBeanName);
        }
    }
}