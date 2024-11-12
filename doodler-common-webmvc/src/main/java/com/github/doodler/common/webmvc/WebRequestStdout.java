package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.DEFAULT_MAXIMUM_RESPONSE_TIME;
import static com.github.doodler.common.Constants.REQUEST_HEADER_REQUEST_ID;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import com.github.doodler.common.context.ApiDebuger;
import com.github.doodler.common.context.ContextPath;
import com.github.doodler.common.context.HttpRequestContextHolder;
import com.github.doodler.common.context.HttpRequestInfo;
import com.github.doodler.common.context.WebRequestCompletionAdvice;
import com.github.doodler.common.webmvc.WebServerConfig.WebRequestLoggerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: WebRequestStdout
 * @Author: Fred Feng
 * @Date: 26/01/2023
 * @Version 1.0.0
 */
@Order(0)
@Slf4j
@RequiredArgsConstructor
public class WebRequestStdout extends WebRequestCompletionAdvice {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final ContextPath contextPath;
    private final WebRequestLoggerProperties loggerProperties;
    private final Marker marker;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doAfterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception e) throws Exception {
        doLog(request, response, handler, e);
    }

    private void doLog(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception e) {
        HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
        long elapsed = System.currentTimeMillis() - httpRequestInfo.getTimestmap();
        Level level = getLoggingLevel(request, response, e, elapsed);
        if (level != null) {
            StringBuilder str = logRequestAndResponse(request, response, handler, e, elapsed);
            switch (level) {
                case ERROR:
                    if (log.isErrorEnabled()) {
                        if (e != null) {
                            log.error(marker, str.toString(), e);
                        } else {
                            log.error(marker, str.toString());
                        }
                    }
                    break;
                case WARN:
                    if (log.isWarnEnabled()) {
                        log.warn(marker, str.toString());
                    }
                    break;
                case INFO:
                    if (log.isInfoEnabled()) {
                        log.info(marker, str.toString());
                    }
                    break;
                case DEBUG:
                    if (log.isDebugEnabled()) {
                        log.debug(marker, str.toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private StringBuilder logRequestAndResponse(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception e, long elapsed) {
        HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
        String requestId = httpRequestInfo.getRequestHeaders().getFirst(REQUEST_HEADER_REQUEST_ID);
        if (StringUtils.isBlank(requestId)) {
            requestId = "Endpoint";
        }
        String handlerDescription = "Unknown";
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            handlerDescription = String.format("[%s] %s#%s(%s args)", requestId,
                    handlerMethod.getBeanType().getSimpleName(),
                    handlerMethod.getMethod().getName(),
                    handlerMethod.getMethod().getParameterCount());
        }
        StringBuilder str = new StringBuilder();
        str.append(NEWLINE);
        log(str, "[%s] <--- HTTP/1.1 %s %s ", handlerDescription, httpRequestInfo.getMethod(),
                httpRequestInfo.getPath());
        for (Map.Entry<String, List<String>> entry : httpRequestInfo.getRequestHeaders()
                .entrySet()) {
            log(str, "[%s] %s: %s", handlerDescription, entry.getKey(), entry.getValue());
        }
        log(str, "[%s] request body: %s", handlerDescription, httpRequestInfo.getRequestBody());
        log(str, "[%s] <--- END HTTP %s (%s ms) ", handlerDescription,
                HttpStatus.valueOf(response.getStatus()), elapsed);
        if (e != null) {
            log(str, "[%s] <--- ERROR %s: %s", handlerDescription, e.getClass().getSimpleName(),
                    e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log(str, "[%s] %s", handlerDescription, sw.toString());
            log(str, "[%s] <--- END ERROR", handlerDescription);
        }
        return str;
    }

    protected void log(StringBuilder str, String format, Object... args) {
        str.append(String.format(format, args));
        str.append(NEWLINE);
    }

    private Level getLoggingLevel(HttpServletRequest request, HttpServletResponse response,
            Exception e, long elapsed) {
        if (HttpStatus.valueOf(response.getStatus()).isError() || e != null) {
            return Level.ERROR;
        } else if (elapsed >= DEFAULT_MAXIMUM_RESPONSE_TIME) {
            return Level.WARN;
        } else if (matchesLoggingUrls(request)) {
            return Level.INFO;
        } else if (ApiDebuger.enableServerSide()) {
            return Level.DEBUG;
        }
        return null;
    }

    private boolean matchesLoggingUrls(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(loggerProperties.getPaths())) {
            return false;
        }
        String requestPath = request.getRequestURI().replaceFirst(contextPath.getContextPath(), "");
        if (loggerProperties.getPaths().contains(requestPath)) {
            return true;
        }
        for (String pathPattern : loggerProperties.getPaths()) {
            if (pathMatcher.match(pathPattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
}
