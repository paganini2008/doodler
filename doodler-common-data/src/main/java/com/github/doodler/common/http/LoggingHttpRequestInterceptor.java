package com.github.doodler.common.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import com.github.doodler.common.utils.Markers;
import com.github.doodler.common.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: LoggingHttpRequestInterceptor
 * @Author: Fred Feng
 * @Date: 25/12/2024
 * @Version 1.0.0
 */
@Slf4j
public class LoggingHttpRequestInterceptor implements ClientHttpRequestInterceptor, Ordered {

    private static final String NEWLINE = System.getProperty("line.separator");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        String handlerDescription = UrlUtils.toHostUrl(request.getURI().toURL()).toString();
        StringBuilder str = new StringBuilder();
        str.append(NEWLINE);
        log(str, "[%s] <--- HTTP/1.1 %s %s ", handlerDescription, request.getMethodValue(),
                request.getURI().getPath());
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            log(str, "[%s] %s: %s", handlerDescription, entry.getKey(), entry.getValue());
        }
        log(str, "[%s] request body: %s", handlerDescription, new String(body));
        long startTime = System.currentTimeMillis();
        Exception reason = null;
        ClientHttpResponse response;
        try {
            response = execution.execute(request, body);
            log(str, "[%s] <--- END HTTP %s (%s ms) ", handlerDescription, response.getStatusCode(),
                    System.currentTimeMillis() - startTime);
            log(str, "[%s] <--- response body: %s (%s bytes) ", handlerDescription,
                    IOUtils.toString(response.getBody(), Charset.defaultCharset()),
                    response.getBody().available());
        } catch (IOException e) {
            reason = e;
            throw e;
        } finally {
            if (reason != null) {
                log(str, "[%s] <--- ERROR %s: %s", handlerDescription,
                        reason.getClass().getSimpleName(), reason.getMessage());
                StringWriter sw = new StringWriter();
                reason.printStackTrace(new PrintWriter(sw));
                log(str, "[%s] %s", handlerDescription, sw.toString());
                log(str, "[%s] <--- END ERROR", handlerDescription);
            }
            if (log.isInfoEnabled()) {
                log.info(Markers.SYSTEM, str.toString());
            }
        }
        return response;
    }

    protected void log(StringBuilder str, String format, Object... args) {
        str.append(String.format(format, args));
        str.append(NEWLINE);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
