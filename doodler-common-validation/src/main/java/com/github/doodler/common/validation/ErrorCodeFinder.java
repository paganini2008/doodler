package com.github.doodler.common.validation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.enums.AppName;

/**
 * @Description: ErrorCodeFinder
 * @Author: Fred Feng
 * @Date: 07/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class ErrorCodeFinder implements InitializingBean {

    private final ConcurrentMap<String, ErrorCode> cache = new ConcurrentHashMap<>();

    public ErrorCodeFinder(String packageName) {
        this.packageName = packageName;
    }

    private final String packageName;

    @Value("${spring.application.name}")
    private String applicationName;

    public boolean containsKey(String messageKey) {
        return cache.containsKey(messageKey);
    }

    public ErrorCode lookup(String messageKey) {
        return cache.get(messageKey);
    }

    public void add(ErrorCode errorCode) {
        if (errorCode != null) {
            cache.putIfAbsent(errorCode.getMessageKey(), errorCode);
        }
    }

    public int size() {
        return cache.size();
    }

    private void addCache(Class<?> errorCodesClass) {
        ReflectionUtils.doWithFields(errorCodesClass, f -> {
            Object result = f.get(null);
            if (result instanceof ErrorCode) {
                ErrorCode errorCode = (ErrorCode) result;
                cache.putIfAbsent(errorCode.getMessageKey(), errorCode);
            }
        }, f -> f.getModifiers() == 25);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AppName appName;
        try{
        	appName = AppName.get(applicationName);
        }catch (RuntimeException e) {
			return;
		}
        String targetClassName = String.format("%s.%sErrorCodes", packageName,
                StringUtils.capitalize(appName.getShortName()));
        Class<?> errorCodesClass = null;
        if (ClassUtils.isPresent(targetClassName, Thread.currentThread().getContextClassLoader())) {
            try {
                errorCodesClass = Class.forName(targetClassName);
            } catch (Exception ignored) {
            }
        }
        if (errorCodesClass != null) {
            addCache(errorCodesClass);
            if (log.isInfoEnabled()) {
                log.info("Find {} error code(s)", size());
            }
        }
    }
}