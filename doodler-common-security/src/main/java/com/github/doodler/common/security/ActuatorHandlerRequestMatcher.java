package com.github.doodler.common.security;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @Description: ActuatorHandlerRequestMatcher
 * @Author: Fred Feng
 * @Date: 15/04/2023
 * @Version 1.0.0
 */
public class ActuatorHandlerRequestMatcher implements RequestMatcher {

    private static final String PATH_PATTERN_MONITOR = "/monitor/**";

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final int port;
    private final int actuatorPort;

    public ActuatorHandlerRequestMatcher(int port, int actuatorPort) {
        this.port = port;
        this.actuatorPort = actuatorPort;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (actuatorPort == port) {
            return false;
        }
        if (request.getServerPort() == actuatorPort) {
            String requestUri = request.getRequestURI();
            return !pathMatcher.match(PATH_PATTERN_MONITOR, requestUri);
        }
        return false;
    }
}