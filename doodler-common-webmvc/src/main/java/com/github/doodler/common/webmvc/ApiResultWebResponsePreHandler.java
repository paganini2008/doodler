package com.github.doodler.common.webmvc;

import static com.github.doodler.common.Constants.REQUEST_HEADER_TIMESTAMP;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.HttpRequestContextHolder;

/**
 * @Description: ApiResultWebResponsePreHandler
 * @Author: Fred Feng
 * @Date: 25/02/2023
 * @Version 1.0.0
 */
@Order(0)
@Component
public class ApiResultWebResponsePreHandler implements WebResponsePreHandler {

	@Override
	public boolean supports(Class<?> resultClass, HttpServletRequest request, HttpServletResponse response) {
		return resultClass.equals(ApiResult.class) && (request != null && response != null);
	}

	@Override
	public Object beforeBodyWrite(Object body, HttpServletRequest request, HttpServletResponse response) {
		String serverHost = request.getHeader("Server-Host-Url");
		if(StringUtils.isNotBlank(serverHost)) {
			response.addHeader("Server-Host-Url", serverHost);
		}

		ApiResult<?> apiResult = (ApiResult<?>) body;
		if (apiResult.getElapsed() == 0) {
			String timestamp = HttpRequestContextHolder.getHeader(REQUEST_HEADER_TIMESTAMP);
			if (StringUtils.isNotBlank(timestamp)) {
				apiResult.setElapsed(System.currentTimeMillis() - Long.parseLong(timestamp));
			}
		}
		if (StringUtils.isBlank(apiResult.getRequestPath())) {
			apiResult.setRequestPath(request.getRequestURI());
		}
		return apiResult;
	}
}