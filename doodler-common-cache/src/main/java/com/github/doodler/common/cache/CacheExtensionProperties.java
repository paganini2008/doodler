package com.github.doodler.common.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: CacheExtensionProperties
 * @Author: Fred Feng
 * @Date: 30/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("spring.cache.extension")
public class CacheExtensionProperties {

    private List<String> sharedApplicationNames = new ArrayList<>();
    
    private List<String> excludedCacheNames = new ArrayList<>();
}