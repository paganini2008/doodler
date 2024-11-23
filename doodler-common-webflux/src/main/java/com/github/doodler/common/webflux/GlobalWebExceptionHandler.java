package com.github.doodler.common.webflux;

import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.ErrorCode;
import com.github.doodler.common.ExceptionDescriptor;
import com.github.doodler.common.ThrowableInfo;
import com.github.doodler.common.context.MessageLocalization;
import com.github.doodler.common.utils.ExceptionUtils;

import reactor.core.publisher.Mono;

/**
 * 
 * @Description: GlobalWebExceptionHandler
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
public class GlobalWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public GlobalWebExceptionHandler(ErrorAttributes errorAttributes,
                                     Resources resources,
                                     ErrorProperties errorProperties,
                                     ApplicationContext applicationContext,
                                     ApiExceptionContext apiExceptionContext,
                                     MessageLocalization messageLocalization) {
        super(errorAttributes, resources, errorProperties, applicationContext);
        this.apiExceptionContext = apiExceptionContext;
        this.messageLocalization = messageLocalization;
    }

    private final ApiExceptionContext apiExceptionContext;
    private final MessageLocalization messageLocalization;

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = getErrorAttributes(request,
                (includeStackTrace) ? ErrorAttributeOptions.of(Include.STACK_TRACE) : ErrorAttributeOptions.defaults());
        Throwable e = getError(request);
        if (e != null) {
            apiExceptionContext.getExceptionTraces().add(new ThrowableInfo(request.uri().getRawPath(), e.getMessage(),
                    ExceptionUtils.toArray(e), LocalDateTime.now()));
        }
        return ServerResponse.status(super.getHttpStatus(error)).contentType(MediaType.APPLICATION_JSON).body(
                BodyInserters.fromValue(getMessageBody(request, e)));
    }

    private ApiResult<String> getMessageBody(ServerRequest request, Throwable e) {
        ApiResult<String> result;
        if (e instanceof ExceptionDescriptor) {
            ExceptionDescriptor descriptor = (ExceptionDescriptor) e;
            ErrorCode errorCode = descriptor.getErrorCode();
            String lang = request.headers().firstHeader("lang");
            result = ApiResult.failed(messageLocalization.getMessage(errorCode, LocaleUtils.toLocale(lang)));
        } else {
            result = ApiResult.failed(e.getMessage());
        }
        result.setRequestPath(request.uri().getRawPath());

        if (StringUtils.isNotBlank(request.headers().firstHeader(REQUEST_HEADER_TIMESTAMP))) {
            long timestamp = Long.parseLong(request.headers().firstHeader(REQUEST_HEADER_TIMESTAMP));
            result.setElapsed(System.currentTimeMillis() - timestamp);
        }
        return result;
    }

}
