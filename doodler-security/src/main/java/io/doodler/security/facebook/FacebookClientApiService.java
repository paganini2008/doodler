package io.doodler.security.facebook;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @Description: FacebookClientApiService
 * @Author: Fred Feng
 * @Date: 02/12/2022
 * @Version 1.0.0
 */
public interface FacebookClientApiService {

	@Headers({"Content-Type: application/json", "Accept: application/json"})
	@RequestLine("GET /me?fields=id,name,email&access_token={accessToken}")
	FacebookProfile getUserInfo(@Param("accessToken") String accessToken);
}