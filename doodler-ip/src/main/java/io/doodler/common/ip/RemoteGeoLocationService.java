package io.doodler.common.ip;

import org.springframework.cache.annotation.Cacheable;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.doodler.common.ApiResult;
import io.doodler.common.annotations.Ttl;
import io.doodler.common.feign.RestClient;

/**
 * @Description: RemoteGeoLocationService
 * @Author: Fred Feng
 * @Date: 06/12/2022
 * @Version 1.0.0
 */
@RestClient(serviceId = "crypto-common-service")
public interface RemoteGeoLocationService {

	@Ttl(300)
	@Cacheable(cacheNames = "geo",keyGenerator = "geoCacheKeyGenerator")
	@Headers({"Content-Type: application/json", "Accept: application/json"})
	@RequestLine("GET /common/geo?ip={ip}")
	ApiResult<GeoLocationVo> getGeoLocation(@Param("ip") String ip);

}
