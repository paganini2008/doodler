package com.github.doodler.common.ip;

import java.util.Collections;
import org.springframework.http.ResponseEntity;
import com.github.doodler.common.http.RestTemplateHolder;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: DefaultGeoLocationService
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultGeoLocationService implements GeoLocationService {

    private static final String IP_ACCESS_URL_PATTERN = "http://ip-api.com/json/%s";

    private final RestTemplateHolder restTemplateHolder;

    @Override
    public GeoLocationVo getGeoLocation(String ipAddress) {
        ResponseEntity<GeoLocationVo> responseEntity = restTemplateHolder.getRetryableRestTemplate()
                .getForEntity(String.format(IP_ACCESS_URL_PATTERN, ipAddress), GeoLocationVo.class,
                        Collections.emptyMap());
        return responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()
                ? responseEntity.getBody()
                : GeoLocationVo.EMPTY;
    }
}
