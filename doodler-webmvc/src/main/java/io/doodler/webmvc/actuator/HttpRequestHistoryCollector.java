package io.doodler.webmvc.actuator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import io.doodler.common.context.HttpRequestContextHolder;
import io.doodler.common.context.HttpRequestInfo;
import io.doodler.common.context.WebRequestCompletionAdvice;
import io.doodler.common.utils.LatestRequestHistory;

/**
 * @Description: HttpRequestHistoryCollector
 * @Author: Fred Feng
 * @Date: 17/04/2023
 * @Version 1.0.0
 */
@Component
public class HttpRequestHistoryCollector extends WebRequestCompletionAdvice {

	private final LatestRequestHistory<HttpRequestInfo> latest2xx = new LatestRequestHistory<>(128);
	private final LatestRequestHistory<HttpRequestInfo> latestErrors = new LatestRequestHistory<>(128);

	@Override
	protected void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {
		HttpRequestInfo httpRequestInfo = HttpRequestContextHolder.get();
		if (httpRequestInfo.getStatus().is2xxSuccessful()) {
			latest2xx.add(httpRequestInfo);
		} else if (httpRequestInfo.getStatus().isError()) {
			latestErrors.add(httpRequestInfo);
		}
	}

	public List<HttpRequestInfo> showHistory() {
		return latest2xx.display();
	}

	public List<HttpRequestInfo> showErrorHistory() {
		return latestErrors.display();
	}
}