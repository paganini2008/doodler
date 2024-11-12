package com.github.doodler.common.feign;

/**
 * @Description: RestClientConstants
 * @Author: Fred Feng
 * @Date: 29/11/2022
 * @Version 1.0.0
 */
public interface RestClientConstants {

    String REQUEST_HEADER_TIMESTAMP = "timestamp";

    String DEFAULT_LOGGER_NAME = "com.github.doodler.common.feign.RestClient";

    long DEFAULT_MAXIMUM_RESPONSE_TIME = 3L * 1000;
}
