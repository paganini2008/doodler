package com.github.doodler.common.cloud.redis;

import java.net.URI;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.github.doodler.common.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: HttpPing
 * @Author: Fred Feng
 * @Date: 05/08/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HttpPing implements Ping {

    private final RestTemplate restTemplate = new RestTemplate();
    private final boolean usePublicIp;

    @Override
    public boolean isAlive(ServiceInstance obj) {
        ApplicationInstance instance = (ApplicationInstance) obj;
        URI pingUri = instance.createUri(usePublicIp, instance.getContextPath(), "ping");
        long startTime = System.currentTimeMillis();
        boolean testOk = testConnection(pingUri);
        if (log.isTraceEnabled()) {
            log.trace("[HttpPing] Request to url: {}, result: {}, take: {}", pingUri, testOk,
                    (System.currentTimeMillis() - startTime));
        }
        return testOk;
    }

    @SuppressWarnings("rawtypes")
    protected boolean testConnection(URI uri) {
        try {
            ResponseEntity<ApiResult> responseEntity =
                    restTemplate.getForEntity(uri, ApiResult.class);
            return responseEntity.getBody() != null
                    && "UP".equalsIgnoreCase((String) responseEntity.getBody().getData());
        } catch (Exception e) {
            return false;
        }
    }
}
