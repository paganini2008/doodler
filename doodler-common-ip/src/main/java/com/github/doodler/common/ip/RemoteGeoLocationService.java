package com.github.doodler.common.ip;

import org.springframework.cache.annotation.Cacheable;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.annotations.Ttl;
import com.github.doodler.common.feign.RestClient;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @Description: RemoteGeoLocationService
 * @Author: Fred Feng
 * @Date: 06/12/2022
 * @Version 1.0.0
 */
@RestClient(serviceId = "doodler-common-service")
public interface RemoteGeoLocationService {

    @Ttl(300)
    @Cacheable(cacheNames = "geo", keyGenerator = "geoCacheKeyGenerator")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("GET /common/geo?ip={ip}")
    ApiResult<GeoLocationVo> getGeoLocation(@Param("ip") String ip);

}
