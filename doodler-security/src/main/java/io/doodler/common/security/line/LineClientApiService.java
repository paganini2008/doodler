package io.doodler.common.security.line;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @Description: LineClientApiService
 * @Author: Fred Feng
 * @Date: 11/01/2023
 * @Version 1.0.0
 */
@Headers("Authorization: Bearer {accessToken}")
public interface LineClientApiService {

	@Headers({"Content-Type: application/json", "Accept: application/json"})
	@RequestLine("GET /oauth2/v2.1/userinfo")
	LineProfile getUserInfo(@Param("accessToken") String accessToken);
}