package com.github.doodler.common.security.google;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @Description: GoogleClientApiService
 * @Author: Fred Feng
 * @Date: 30/07/2020
 * @Version 1.0.0
 */
public interface GoogleClientApiService {

	@Headers({"Content-Type: application/json", "Accept: application/json"})
	@RequestLine("GET /oauth2/v2/userinfo?access_token={accessToken}")
	GoogleProfile getUserInfo(@Param("accessToken") String accessToken);
}