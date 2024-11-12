package com.github.doodler.common.feign;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import com.github.doodler.common.context.GenericBeanDefinitionRegistrarSupport;

/**
 * @Description: RestClientPackageScanningRegistrar
 * @Author: Fred Feng
 * @Date: 05/12/2022
 * @Version 1.0.0
 */
public class RestClientPackageScanningRegistrar extends GenericBeanDefinitionRegistrarSupport
        implements Ordered {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerRestClients(metadata, registry);
    }

    private void registerRestClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        List<String> basePackages = getBasePackages(metadata);
        if (basePackages.isEmpty()) {
            return;
        }
        RestClientClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new RestClientClassPathBeanDefinitionScanner(
                registry);
        if (resourceLoader != null) {
            classPathBeanDefinitionScanner.setResourceLoader(resourceLoader);
        }
        classPathBeanDefinitionScanner.setBeanFactory(beanFactory);
        classPathBeanDefinitionScanner.setEnvironment(environment);
        classPathBeanDefinitionScanner.setBeanClassLoader(beanClassLoader);

        classPathBeanDefinitionScanner.scan(basePackages.toArray(new String[0]));
    }

    private List<String> getBasePackages(AnnotationMetadata metadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(EnableRestClientEndpoints.class.getName()));
        List<String> basePackages = new ArrayList<String>();
        if (annotationAttributes.containsKey("basePackages")) {
            for (String basePackage : annotationAttributes.getStringArray("basePackages")) {
                if (StringUtils.isNotBlank(basePackage)) {
                    basePackages.add(basePackage);
                }
            }
        }
        return basePackages;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 90;
    }
}