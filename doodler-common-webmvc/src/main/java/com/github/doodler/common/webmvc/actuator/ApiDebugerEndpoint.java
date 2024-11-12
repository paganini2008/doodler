package com.github.doodler.common.webmvc.actuator;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.context.ApiDebuger;

/**
 * @Description: ApiDebugerEndpoint
 * @Author: Fred Feng
 * @Date: 24/05/2023
 * @Version 1.0.0
 */
@Component
@RestControllerEndpoint(id = "apiDebuger")
public class ApiDebugerEndpoint {

	@PostMapping("/enableServerSide")
	public ApiResult<String> enableServerSide(@RequestParam(name = "enabled") boolean enabled) {
		ApiDebuger.enableServerSide(enabled);
		return ApiResult.ok();
	}

	@PostMapping("/enableRestClient")
	public ApiResult<String> enableRestClient(@RequestParam(name = "enabled") boolean enabled) {
		ApiDebuger.enableRestClient(enabled);
		return ApiResult.ok();
	}
}