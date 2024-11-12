package com.github.doodler.common.feign.statistics;

import com.github.doodler.common.feign.HttpUtils;
import com.github.doodler.common.feign.RestClientInterceptor;
import com.github.doodler.common.utils.ExceptionUtils;
import com.github.doodler.common.utils.LatestRequestHistory;
import feign.Request;
import feign.Response;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * @Description: LatestRequestHistoryCollector
 * @Author: Fred Feng
 * @Date: 27/01/2023
 * @Version 1.0.0
 */
public class LatestRequestHistoryCollector implements RestClientInterceptor {

    private final LatestRequestHistory<RequestHistory> latest2xx = new LatestRequestHistory<>(128);
    private final LatestRequestHistory<RequestHistory> latestErrors = new LatestRequestHistory<>(128);

    @Override
    public void postHandle(Request request, Response response) {
        RequestHistory requestHistory = new RequestHistory(request);
        requestHistory.setStatus(response.status());
        requestHistory.setResponseHeaders(HttpUtils.getHttpHeaders(response).toSingleValueMap());
        if (HttpStatus.valueOf(requestHistory.getStatus()).is2xxSuccessful()) {
            latest2xx.add(requestHistory);
        } else if (HttpStatus.valueOf(requestHistory.getStatus()).isError()) {
            latestErrors.add(requestHistory);
        }
    }

    @Override
    public void afterCompletion(Request request, Response response, Exception e) {
        RequestHistory requestHistory = new RequestHistory(request);
        requestHistory.setStatus(response.status());
        requestHistory.setResponseHeaders(HttpUtils.getHttpHeaders(response).toSingleValueMap());
        if (e != null) {
            requestHistory.setErrors(ExceptionUtils.toArray(e));
        }
        if (HttpStatus.valueOf(requestHistory.getStatus()).is2xxSuccessful()) {
            latest2xx.add(requestHistory);
        } else if (HttpStatus.valueOf(requestHistory.getStatus()).isError() || e != null) {
            latestErrors.add(requestHistory);
        }
    }

    public List<RequestHistory> showHistory() {
        return latest2xx.display();
    }

    public List<RequestHistory> showErrorHistory() {
        return latestErrors.display();
    }
}