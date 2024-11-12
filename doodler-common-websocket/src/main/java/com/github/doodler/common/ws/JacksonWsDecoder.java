package com.github.doodler.common.ws;

import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.doodler.common.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @Description: JacksonWsDecoder
 * @Author: Fred Feng
 * @Date: 12/03/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class JacksonWsDecoder implements WsDecoder {

    private static final String REQUEST_PATH_PATTERN = "%s/ws/%s/";
    private final ObjectMapper objectMapper;

    @Value("${spring.mvc.servlet.path:}")
    private String servletContextPath;

    @SneakyThrows
    @Override
    public String decode(String channel, WsUser user, Object payload, long timestamp) {
        ApiResult<?> apiResult = wrapperPayload(payload);
        apiResult.setElapsed(System.currentTimeMillis() - timestamp);
        apiResult.setRequestPath(String.format(REQUEST_PATH_PATTERN, servletContextPath, channel));
        return objectMapper.writeValueAsString(apiResult);
    }

    protected ApiResult<?> wrapperPayload(Object payload) {
        if (!(payload instanceof ApiResult)) {
            return ApiResult.ok(payload);
        }
        return (ApiResult<?>) payload;
    }
}