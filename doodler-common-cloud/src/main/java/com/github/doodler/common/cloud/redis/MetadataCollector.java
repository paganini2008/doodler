package com.github.doodler.common.cloud.redis;

import java.util.Map;

/**
 * 
 * @Description: MetadataCollector
 * @Author: Fred Feng
 * @Date: 10/08/2024
 * @Version 1.0.0
 */
@FunctionalInterface
public interface MetadataCollector {

    Map<String, String> getMetadataMap();

}
