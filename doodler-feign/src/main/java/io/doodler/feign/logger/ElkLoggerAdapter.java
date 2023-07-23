package io.doodler.feign.logger;

import static io.doodler.feign.RestClientConstants.DEFAULT_LOGGER_NAME;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import feign.Request;
import io.doodler.common.utils.IdUtils;
import io.doodler.common.utils.Markers;
import io.doodler.feign.RestClientInterceptor;
import io.doodler.feign.RestClientUtils;

/**
 * @Description: ElkLoggerAdapter
 * @Author: Fred Feng
 * @Date: 29/05/2023
 * @Version 1.0.0
 */
public class ElkLoggerAdapter implements RestClientInterceptor, InitializingBean {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void afterPropertiesSet() throws Exception {
        RestClientUtils.setDefaultLogger(new ElkLogger(DEFAULT_LOGGER_NAME, Markers.forName(applicationName)));
        RestClientUtils.addRestClientInterceptor(this);
    }

    @Override
    public void preHandle(Request request) {
        String guid = IdUtils.getShortUuid();
        request.requestTemplate().header("guid", guid);
    }
}