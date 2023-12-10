package io.doodler.common.webmvc;

import org.springframework.stereotype.Component;

import io.doodler.common.context.HttpRequestContextHolder;
import io.doodler.common.context.HttpRequestInfo;
import io.doodler.common.context.RequestContextExchanger;

/**
 * 
 * @Description: HttpRequestInfoExchanger
 * @Author: Fred Feng
 * @Date: 21/09/2023
 * @Version 1.0.0
 */
@Component
public class HttpRequestInfoExchanger implements RequestContextExchanger<HttpRequestInfo>{

	@Override
	public HttpRequestInfo get() {
		return HttpRequestContextHolder.get();
	}

	@Override
	public void set(HttpRequestInfo obj) {
		HttpRequestContextHolder.set(obj);
	}

	@Override
	public void reset() {
		HttpRequestContextHolder.clear();
	}

}
