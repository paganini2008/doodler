package io.doodler.feign.actuator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import io.doodler.common.ApiResult;
import io.doodler.feign.RestClientInfo;
import io.doodler.feign.RestClientInfoCollector;

/**
 * @Description: RestClientInfoEndpoint
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@Endpoint(id = "restClientInfos")
public class RestClientInfoEndpoint {

	@Autowired
	private RestClientInfoCollector restClientInfoCollector;

	@ReadOperation
	public ApiResult<List<RestClientInfo>> infos() {
		return ApiResult.ok(restClientInfoCollector.infos());
	}
}