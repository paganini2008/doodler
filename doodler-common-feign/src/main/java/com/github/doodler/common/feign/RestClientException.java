package com.github.doodler.common.feign;

import feign.Request;
import feign.Response;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import com.github.doodler.common.BizException;
import com.github.doodler.common.ErrorCode;

/**
 * @Description: RestClientException
 * @Author: Fred Feng
 * @Date: 25/04/2023
 * @Version 1.0.0
 */
public class RestClientException extends BizException {

    private static final long serialVersionUID = 4860223774060320821L;

    public RestClientException(Request request, Response response, ErrorCode errorCode, HttpStatus httpStatus, Object arg) {
        super(errorCode, httpStatus, arg);
        this.request = request;
        this.response = response;
    }

    public RestClientException(Request request, Response response, ErrorCode errorCode, HttpStatus httpStatus,
                               Throwable e, Object arg) {
        super(errorCode, httpStatus, e, arg);
        this.request = request;
        this.response = response;
    }

    private final Request request;
    private final @Nullable Response response;

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}