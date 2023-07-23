package io.doodler.webmvc.actuator;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.doodler.common.ApiResult;
import io.doodler.common.context.AppLogSwitch;

/**
 * @Description: AppLogSwitchEndpoint
 * @Author: Fred Feng
 * @Date: 24/05/2023
 * @Version 1.0.0
 */
@Component
@RestControllerEndpoint(id = "appLogSwitch")
public class AppLogSwitchEndpoint {

	@GetMapping("/enableLocalAppLog")
	public ApiResult<String> enableLocalAppLog(@RequestParam(name = "enabled") boolean enabled) {
		AppLogSwitch.setLocal(enabled);
		return ApiResult.ok();
	}

	@GetMapping("/enableFeignAppLog")
	public ApiResult<String> enableFeignAppLog(@RequestParam(name = "enabled") boolean enabled) {
		AppLogSwitch.setFeign(enabled);
		return ApiResult.ok();
	}
}