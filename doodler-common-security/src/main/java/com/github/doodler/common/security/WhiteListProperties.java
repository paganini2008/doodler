package com.github.doodler.common.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.github.doodler.common.context.ContextPath;
import cn.hutool.core.net.Ipv4Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WhiteListProperties
 * @Author: Fred Feng
 * @Date: 19/11/2022
 * @Version 1.0.0
 */
@Slf4j
@ConfigurationProperties("spring.security.client")
@RequiredArgsConstructor
public class WhiteListProperties implements InitializingBean {

    private static final String PATH_VARS_PATTERN = "\\{(.*?)\\}";

    private final WebApplicationContext applicationContext;

    @Getter
    @Setter
    private String ipRange;

    @Getter
    @Setter
    private List<String> ipAddresses;

    @Getter
    private List<WhiteListInfo> whiteListUrls = new ArrayList<WhiteListInfo>();

    @Override
    public void afterPropertiesSet() {
        RequestMappingHandlerMapping mapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        ContextPath contextPath = applicationContext.getBean(ContextPath.class);
        Map<RequestMappingInfo, HandlerMethod> requestMapper = mapping.getHandlerMethods();
        final String prefix =
                StringUtils.isNotBlank(contextPath.getContextPath()) ? contextPath.getContextPath()
                        : "";
        requestMapper.keySet().forEach(info -> {
            HandlerMethod handlerMethod = requestMapper.get(info);

            WhiteList method =
                    AnnotationUtils.findAnnotation(handlerMethod.getMethod(), WhiteList.class);
            Optional.ofNullable(method)
                    .ifPresent(c -> info.getPatternsCondition().getPatterns()
                            .forEach(url -> whiteListUrls.add(createWhiteListInfo(
                                    prefix + url.replaceAll(PATH_VARS_PATTERN, "*"),
                                    method.mode()))));

            WhiteList controller =
                    AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), WhiteList.class);
            Optional.ofNullable(controller)
                    .ifPresent(c -> info.getPatternsCondition().getPatterns()
                            .forEach(url -> whiteListUrls.add(createWhiteListInfo(
                                    prefix + url.replaceAll(PATH_VARS_PATTERN, "*"),
                                    controller.mode()))));
        });
        log.info("White List Urls: {}", whiteListUrls);
    }

    private WhiteListInfo createWhiteListInfo(String urlPattern, Mode mode) {
        WhiteListInfo info = new WhiteListInfo(urlPattern, mode);
        if (CollectionUtils.isNotEmpty(ipAddresses)) {
            info.setIpAddresses(ipAddresses);
        }
        if (StringUtils.isNotBlank(ipRange)) {
            info.setIpRange(ipRange);
        }
        return info;
    }

    public static enum Mode {

        INTERNAL, EXTERNAL;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class WhiteListInfo {

        private String urlPattern;
        private Mode mode;
        private List<String> ipAddresses = new ArrayList<>();

        public WhiteListInfo(String urlPattern, Mode mode) {
            this.urlPattern = urlPattern;
            this.mode = mode;
        }

        public void setIpRange(String ipRange) {
            this.ipAddresses.addAll(Ipv4Util.list(ipRange, true));
        }
    }
}
