package com.github.doodler.common.feign.logger;

import org.springframework.beans.factory.InitializingBean;
import com.github.doodler.common.feign.RestClientInterceptor;
import com.github.doodler.common.feign.RestClientUtils;
import com.github.doodler.common.utils.IdUtils;
import feign.Request;

/**
 * @Description: ElkLoggerAdapter
 * @Author: Fred Feng
 * @Date: 29/05/2023
 * @Version 1.0.0
 */
public class ElkLoggerAdapter implements RestClientInterceptor, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        RestClientUtils.addRestClientInterceptor(this);
    }

    @Override
    public void preHandle(Request request) {
        String guid = IdUtils.getShortUuid();
        request.requestTemplate().header("guid", guid);
    }
}