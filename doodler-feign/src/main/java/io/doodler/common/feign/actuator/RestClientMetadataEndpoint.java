package io.doodler.common.feign.actuator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import io.doodler.common.ApiResult;
import io.doodler.common.feign.RestClientMetadata;
import io.doodler.common.feign.RestClientMetadataCollector;

/**
 * @Description: RestClientMetadataCollector
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@Endpoint(id = "restClientMetadata")
public class RestClientMetadataEndpoint {

	@Autowired
	private RestClientMetadataCollector restClientMetadataCollector;

	@ReadOperation
	public ApiResult<List<RestClientMetadata>> metadatas() {
		return ApiResult.ok(restClientMetadataCollector.metadatas());
	}
}