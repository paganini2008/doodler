package io.doodler.common.webmvc.actuator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import io.doodler.common.Constants;

/**
 * @Description: WebAppInfo
 * @Author: Fred Feng
 * @Date: 24/01/2023
 * @Version 1.0.0
 */
@Component
public class WebAppInfo implements InfoContributor {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void contribute(Builder builder) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("project", Constants.PROJECT_NAME);
        info.put("version", Constants.VERSION);
        info.put("name", applicationName);
        builder.withDetail("app", info);
    }
}