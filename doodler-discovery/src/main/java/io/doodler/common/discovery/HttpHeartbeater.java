package io.doodler.common.discovery;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.doodler.common.ApiResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: HttpHeartbeater
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HttpHeartbeater implements Heartbeater {

    private final RestTemplate restTemplate;
    private final boolean usePublicIp;

    @Override
    public boolean isAlive(ApplicationInfo info) {
    	String hostUrl = info.toHostUrl(usePublicIp);
        if(StringUtils.isNotBlank(info.getContextPath())) {
        	hostUrl += info.getContextPath();
        }
        String pingUrl = hostUrl + "/ping";
        long startTime = System.currentTimeMillis();
        boolean testOk = testConnection(pingUrl);
        if (log.isTraceEnabled()) {
            log.trace("[Discovery Ping] Request to: {}, result: {}, take: {}", pingUrl, testOk,
                    (System.currentTimeMillis() - startTime));
        }
        return testOk;
    }

    @SuppressWarnings("rawtypes")
    protected boolean testConnection(String url) {
        try {
            ResponseEntity<ApiResult> responseEntity = restTemplate.getForEntity(url, ApiResult.class,
                    Collections.emptyMap());
            return responseEntity.getBody() != null && "UP".equalsIgnoreCase((String) responseEntity.getBody().getData());
        } catch (Exception e) {
            return false;
        }
    }
}