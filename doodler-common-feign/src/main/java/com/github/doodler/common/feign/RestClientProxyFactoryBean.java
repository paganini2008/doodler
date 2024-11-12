package com.github.doodler.common.feign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RestClientProxyFactoryBean
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
public class RestClientProxyFactoryBean<API> implements FactoryBean<API>, 
        InitializingBean,
        BeanNameAware,
        SmartInitializingSingleton {

    private final String serviceId;
    private final Class<API> apiInterfaceClass;
    private final String defaultUrl;
    private final Map<String, Object> metaData;

    public RestClientProxyFactoryBean(String serviceId, Class<API> apiInterfaceClass,
                                      String defaultUrl, Map<String, Object> metaData) {
        this.serviceId = serviceId;
        this.apiInterfaceClass = apiInterfaceClass;
        this.defaultUrl = defaultUrl;
        this.metaData = metaData;
    }

    @Setter
    private String beanName;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RestClientCustomizer restClientCustomizer;
    
    private final List<RestClientInvokerAspect> restClientInvokerAspects = new CopyOnWriteArrayList<>();

    private API instance;

    @Override
    public API getObject() throws Exception {
        return this.instance;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.instance = createInstance();
    }

    private API createInstance() throws Exception {
    	String url;
    	if(StringUtils.isNotBlank(serviceId)) {
    		url = "http://" + serviceId;
    	}else {
    		url = defaultUrl;
    	}
    	
    	API instance = RestClientProxyBuilder.rpc(apiInterfaceClass).setProvider(url)
        .postConfigurer(
                builder -> restClientCustomizer.customize(builder, serviceId, beanName, apiInterfaceClass,
                        metaData)).build();
    	return (API) ApiProxyUtils.getProxyInstance(apiInterfaceClass,
				new DefaultRestClientInvokerBean<API>(apiInterfaceClass, instance, restClientInvokerAspects, getFallbackFactorySupplier()));
    }

    private Supplier<FallbackFactory<API>> getFallbackFactorySupplier() {
        Supplier<FallbackFactory<API>> fallbackFactorySupplier = null;
        final Class<?> fallbackFactoryClass = (Class<?>) metaData.get("fallbackFactory");
        final Class<?> fallbackClass = (Class<?>) metaData.get("fallback");

        if (fallbackFactoryClass != null && !fallbackFactoryClass.equals(void.class)) {
            if (GenericTypeFallbackFactory.class.isAssignableFrom(fallbackFactoryClass)) {
                fallbackFactorySupplier = () -> {
                    return getFallbackFactory(fallbackFactoryClass, apiInterfaceClass);
                };
            } else {
                fallbackFactorySupplier = () -> {
                    return getFallbackFactory(fallbackFactoryClass);
                };
            }
        } else if (fallbackClass != null && !fallbackClass.equals(void.class)) {
            fallbackFactorySupplier = () -> {
                return new DefaultFallbackFactory<>((Class<API>) fallbackClass, applicationContext);
            };
        }
        return fallbackFactorySupplier;
    }

    private <F extends FallbackFactory<API>> F getFallbackFactory(Class<?> fallbackFactoryClass, Object... args) {
        try {
            return (F) applicationContext.getBean(fallbackFactoryClass);
        } catch (RuntimeException e) {
            try {
                if (ArrayUtils.isNotEmpty(args)) {
                    return (F) ConstructorUtils.invokeConstructor(fallbackFactoryClass, args);
                }
                return (F) ConstructorUtils.invokeConstructor(fallbackFactoryClass);
            } catch (Exception ee) {
                if (log.isErrorEnabled()) {
                    log.error(ee.getMessage(), ee);
                }
                return null;
            }
        }
    }

    @Override
    public Class<?> getObjectType() {
        return apiInterfaceClass;
    }

    @Override
    public void afterSingletonsInstantiated() {
        final Object apiInstance = applicationContext.getBean(apiInterfaceClass);
        Map<String, InitializingRestClientBean> restClientBeans = applicationContext.getBeansOfType(
                InitializingRestClientBean.class);
        if (MapUtils.isNotEmpty(restClientBeans)) {
            restClientBeans.values().forEach(restClientBean -> {
                if (restClientBean.supports(apiInterfaceClass, beanName)) {
                    restClientBean.initialize(apiInstance, apiInterfaceClass, beanName);
                }
            });
        }
        
		Map<String, RestClientInvokerAspect> aspects = applicationContext.getBeansOfType(RestClientInvokerAspect.class);
		if (MapUtils.isNotEmpty(aspects)) {
			restClientInvokerAspects.addAll(new ArrayList<>(aspects.values()));
		}
    }
}