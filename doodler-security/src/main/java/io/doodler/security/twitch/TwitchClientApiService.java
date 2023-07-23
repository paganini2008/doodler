package io.doodler.security.twitch;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @Description: TwitchClientApiService
 * @Author: Fred Feng
 * @Date: 10/01/2023
 * @Version 1.0.0
 */
@Headers("Authorization: Bearer {accessToken}")
public interface TwitchClientApiService {

	@Headers({"Content-Type: application/json", "Accept: application/json"})
	@RequestLine("GET /oauth2/userinfo")
	TwitchProfile getUserInfo(@Param("accessToken") String accessToken);
}