package com.github.doodler.common.context;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import lombok.Getter;

/**
 * 
 * @Description: ContextPath
 * @Author: Fred Feng
 * @Date: 21/10/2024
 * @Version 1.0.0
 */
@Getter
public final class ContextPath implements Ordered {

    @Value("${server.servlet.context-path:}")
    private String servletContextPath;

    @Value("${spring.mvc.servlet.path:}")
    private String apiContextPath;

    private String value;

    public String getApiContextPath() {
        return apiContextPath;
    }

    public String getServletContextPath() {
        return servletContextPath;
    }

    public String getContextPath() {
        if (StringUtils.isBlank(value)) {
            String path = "";
            if (StringUtils.isNotBlank(servletContextPath)) {
                path += cleanPath(servletContextPath);
            }
            if (StringUtils.isNotBlank(apiContextPath)) {
                path += cleanPath(apiContextPath);
            }
            this.value = path;
        }
        return value;
    }

    public String retrivePath(String path) {
        path = cleanPath(path);
        return getContextPath().concat(path);
    }

    public String[] retrivePaths(String[] paths) {
        String contextPath = getContextPath();
        return Arrays.stream(paths).map(p -> contextPath.concat(cleanPath(p)))
                .toArray(i -> new String[i]);
    }

    static String cleanPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
