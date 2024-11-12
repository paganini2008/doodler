package com.github.doodler.common.feign;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import lombok.Setter;

/**
 * @Description: RestClientClassPathBeanDefinitionScanner
 * @Author: Fred Feng
 * @Date: 05/12/2022
 * @Version 1.0.0
 */
public class RestClientClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private final Log log = LogFactory.getLog(RestClientClassPathBeanDefinitionScanner.class);

    public RestClientClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }
    
    @Setter
    private BeanFactory beanFactory;

    @Setter
    private Environment environment;

    @Setter
    private ClassLoader beanClassLoader;

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        addIncludeFilter(new AnnotationTypeFilter(RestClient.class));
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.size() > 0) {
            processBeanDefinitions(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        final BeanDefinitionRegistry registry = getRegistry();
        final String currentApplicationName = environment.getRequiredProperty("spring.application.name");
        ScannedGenericBeanDefinition beanDefinition;
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            beanDefinition = ((ScannedGenericBeanDefinition) beanDefinitionHolder.getBeanDefinition());
            String className = beanDefinition.getMetadata().getClassName();
            Class<?> apiInterfaceClass;
            try {
                apiInterfaceClass = beanClassLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(apiInterfaceClass,
                    RestClient.class);
            if (attributes == null) {
                throw new IllegalStateException("Invalid class of RestClient: " + apiInterfaceClass);
            }
            String possibleBeanName = Introspector.decapitalize(apiInterfaceClass.getSimpleName());
            if (registry.containsBeanDefinition(className) ||
                    registry.containsBeanDefinition(beanDefinition.getBeanClassName()) ||
                    registry.containsBeanDefinition(
                            possibleBeanName + "." + RestClientProxyFactoryBean.class.getSimpleName())) {
                throw new IllegalStateException(
                        "Duplicated RestClient bean by name '" + className + "' or '" + beanDefinition.getBeanClassName() +
                                "'");
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
            
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(serviceId);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(apiInterfaceClass);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(defaultUrl);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(attr);
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinition.setBeanClass(RestClientProxyFactoryBean.class);

            log.info("Register RestClient: " + possibleBeanName);
        }
    }
}