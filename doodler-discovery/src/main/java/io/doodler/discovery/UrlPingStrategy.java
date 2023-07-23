package io.doodler.discovery;

import static io.doodler.common.Constants.URL_PATTERN_PING;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.doodler.common.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: UrlPingStrategy
 * @Author: Fred Feng
 * @Date: 04/04/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class UrlPingStrategy implements PingStrategy {

    private final RestTemplate restTemplate;
    private final boolean usePublicIp;

    @Override
    public boolean isAlive(ApplicationInfo info) {
        boolean secure = info.isSecure();
        String host = info.getHost();
        String publicIp = info.getPublicIp();
        int port = info.getPort();
        String pingUrl = String.format(URL_PATTERN_PING, secure ? "https" : "http", usePublicIp ? publicIp : host, port,
                info.getContextPath());
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