package com.github.doodler.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import lombok.experimental.UtilityClass;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

/**
 * @Description: BeanCopyUtils
 * @Author: Fred Feng
 * @Date: 23/11/2022
 * @Version 1.0.0
 */
@UtilityClass
public class BeanCopyUtils {

    static {
        mapperFactory = new DefaultMapperFactory.Builder().build();
    }

    private static final MapperFactory mapperFactory;

    public <X, Y> void mapClass(Class<X> x, Class<Y> y, Map<String, String> fieldMapper) {
        mapClass(x, y, builder -> {
            if (MapUtils.isNotEmpty(fieldMapper)) {
                fieldMapper.entrySet().forEach(e -> {
                    builder.field(e.getKey(), e.getValue());
                });
            }
            builder.byDefault().register();
        });
    }

    public <X, Y> void mapClass(Class<X> x, Class<Y> y, Consumer<ClassMapBuilder<X, Y>> consumer) {
        ClassMapBuilder<X, Y> builder = mapperFactory.classMap(x, y);
        if (consumer != null) {
            consumer.accept(builder);
        }
    }

    public Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass,
            PropertyFilter filter) {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(beanClass);
        if (ArrayUtils.isEmpty(pds)) {
            return Collections.emptyMap();
        }
        return Arrays.stream(pds)
                .filter(pd -> (filter == null) || (filter.accept(beanClass, pd.getName(), pd)))
                .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity(),
                        (a, b) -> a, LinkedHashMap::new));
    }

    private void hardCopyProperties(Object original, Map<String, PropertyDescriptor> originalMap,
            Object destination, Map<String, PropertyDescriptor> destinationMap,
            boolean retainNonNull, ConversionService conversionService)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String propertyName;
        Object propertyValue;
        for (Map.Entry<String, PropertyDescriptor> entry : destinationMap.entrySet()) {
            propertyName = entry.getKey();
            if (!originalMap.containsKey(propertyName)) {
                continue;
            }
            try {
                propertyValue = PropertyUtils.getProperty(original, propertyName);
            } catch (RuntimeException ignored) {
                propertyValue = null;
            }
            if (retainNonNull) {
                Object currentPropertyValue = PropertyUtils.getProperty(destination, propertyName);
                if (propertyValue == null && currentPropertyValue != null) {
                    continue;
                }
            }
            Class<?> targetPropertyType = destinationMap.get(propertyName).getPropertyType();
            try {
                propertyValue = targetPropertyType.cast(propertyValue);
            } catch (RuntimeException e) {
                if (conversionService != null) {
                    Class<?> sourcePropertyType = originalMap.get(propertyName).getPropertyType();
                    if (conversionService.canConvert(sourcePropertyType, targetPropertyType)) {
                        try {
                            propertyValue =
                                    conversionService.convert(propertyValue, targetPropertyType);
                        } catch (RuntimeException ignored) {
                            propertyValue = null;
                        }
                    }
                }
            }
            PropertyUtils.setProperty(destination, propertyName, propertyValue);
        }
    }

    public <S, T> T populateBean(S original, T destination) {
        return populateBean(original, destination, null);
    }

    public <S, T> T populateBean(S original, T destination, PropertyFilter filter) {
        return populateBean(original, destination, filter, true);
    }

    public <S, T> T populateBean(S original, T destination, PropertyFilter filter,
            boolean retainNonNull) {
        return populateBean(original, destination, filter, retainNonNull,
                ConvertUtils.getDefaultConversionService());
    }

    public <S, T> T populateBean(S original, T destination, PropertyFilter filter,
            boolean retainNonNull, ConversionService conversionService) {
        Map<String, PropertyDescriptor> originalMap =
                getPropertyDescriptors(original.getClass(), filter);
        Map<String, PropertyDescriptor> destinationMap =
                getPropertyDescriptors(destination.getClass(), filter);
        try {
            hardCopyProperties(original, originalMap, destination, destinationMap, retainNonNull,
                    conversionService);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return destination;
    }

    public <X, Y> Map<String, Object> hasChanged(X x, Y y) {
        return hasChanged(x, y, null);
    }

    public <X, Y> Map<String, Object> hasChanged(X x, Y y, PropertyFilter filter) {
        Map<String, Object> result = new HashMap<>();
        Map<String, PropertyDescriptor> leftMap = getPropertyDescriptors(x.getClass(), filter);
        Map<String, PropertyDescriptor> rightMap = getPropertyDescriptors(y.getClass(), filter);
        String propertyName;
        for (Map.Entry<String, PropertyDescriptor> entry : leftMap.entrySet()) {
            propertyName = entry.getKey();
            if (!rightMap.containsKey(propertyName)) {
                continue;
            }
            try {
                Object leftValue = PropertyUtils.getProperty(x, propertyName);
                Object rightValue = PropertyUtils.getProperty(y, propertyName);
                if (!Objects.equals(leftValue, rightValue)) {
                    result.put(propertyName, rightValue);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return result;
    }

    public <S, T> T copyBean(S source, Class<T> resultClass) {
        if (source == null) {
            return null;
        }
        try {
            return mapperFactory.getMapperFacade().map(source, resultClass);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public <S, T> List<T> copyBeanList(List<S> sources, Class<T> resultClass) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        try {
            return mapperFactory.getMapperFacade().mapAsList(sources, resultClass);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Copy bean properties to another bean
     *
     * @param from
     * @param to
     */
    public void copyBeanProp(Object from, Object to) {
        try {
            copyBeanProp(from, to, "id", "createdAt", "updatedAt");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Copy bean properties to another bean
     *
     * @param from
     * @param to
     * @param ignoreProperties
     */
    public void copyBeanProp(Object from, Object to, String... ignoreProperties) {
        try {
            BeanUtils.copyProperties(from, to, ignoreProperties);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
