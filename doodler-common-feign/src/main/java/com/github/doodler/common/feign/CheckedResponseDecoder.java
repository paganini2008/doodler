package com.github.doodler.common.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.http.HttpStatus;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.SimpleErrorCode;

/**
 * @Description: CheckedResponseDecoder
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
public class CheckedResponseDecoder implements Decoder {

    private final Decoder delegate;
    private final RestClientInterceptorContainer interceptorContainer;

    public CheckedResponseDecoder(Decoder delegate, RestClientInterceptorContainer interceptorContainer) {
        this.delegate = delegate;
        this.interceptorContainer = interceptorContainer;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        Object answer;
        try {
            answer = delegate.decode(response, type);
            if (answer instanceof ApiResult) {
                ApiResult<?> apiResult = (ApiResult<?>) answer;
                if (!apiResult.ifPresent()) {
                    throw new RestClientException(response.request(), response,
                            new SimpleErrorCode(null, apiResult.getCode(), apiResult.getMsg()),
                            HttpUtils.getHttpStatus(response.status()), apiResult.getData());
                }
            }
        } catch (FeignException e) {
            throw new RestClientException(response.request(), response, ErrorCode.restClientError(e),
                    HttpStatus.valueOf(e.status()), e, null);
        } catch (IOException e) {
            throw new RestClientException(response.request(), response, ErrorCode.restClientError(e),
                    HttpStatus.INTERNAL_SERVER_ERROR, e, null);
        } finally {
            if (interceptorContainer != null) {
                interceptorContainer.onPostHandle(response.request(), response);
            }
        }
        return answer;
    }
}