package io.doodler.common.feign;

import io.doodler.common.ApiResult;
import io.doodler.common.ErrorCode;
import io.doodler.common.SimpleErrorCode;
import io.doodler.common.utils.JacksonUtils;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @Description: GlobalErrorDecoder
 * @Author: Fred Feng
 * @Date: 28/11/2022
 * @Version 1.0.0
 */
@Slf4j
public class GlobalErrorDecoder extends ErrorDecoder.Default {

    private static final int[] retryStatusCodes = new int[]{502, 503};

    private final RestClientInterceptorContainer restClientInterceptorContainer;

    public GlobalErrorDecoder() {
        this(null);
    }

    public GlobalErrorDecoder(RestClientInterceptorContainer restClientInterceptorContainer) {
        this.restClientInterceptorContainer = restClientInterceptorContainer;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception e = null;
        try {
            e = super.decode(methodKey, response);
            if (e instanceof RetryableException) {
                return e;
            }
            if (ArrayUtils.contains(retryStatusCodes, response.status())) {
                return new RetryableException(response.status(), "Retry: " + methodKey,
                        response.request().httpMethod(), null, response.request());
            }
            if (e instanceof FeignException) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
                FeignException fe = (FeignException) e;
                if (ArrayUtils.contains(retryStatusCodes, fe.status())) {
                    return new RetryableException(fe.status(), "Retry: " + methodKey,
                            response.request().httpMethod(), null, response.request());
                }

                if (fe.responseBody().isPresent()) {
                    String responseBody = fe.contentUTF8();
                    ApiResult<?> result = null;
                    if (JacksonUtils.containsKeys(responseBody, "code", "msg")) {
                        try {
                            result = JacksonUtils.parseJson(responseBody, ApiResult.class);
                        } catch (RuntimeException ignored) {
                        }
                    }
                    ErrorCode errorCode;
                    if (result != null) {
                        errorCode = new SimpleErrorCode("", result.getCode(), result.getMsg());
                    } else {
                        errorCode = ErrorCode.restClientError(responseBody);
                    }
                    return new RestClientException(response.request(), response, errorCode, HttpUtils.getHttpStatus(fe), fe,
                            null);
                } else {
                    return new RestClientException(response.request(), response, ErrorCode.restClientError(fe),
                            HttpUtils.getHttpStatus(fe), fe, null);
                }
            }
            return e;
        } finally {
            if (restClientInterceptorContainer != null) {
                restClientInterceptorContainer.onAfterCompletion(response.request(), response, e);
            }
        }
    }
}