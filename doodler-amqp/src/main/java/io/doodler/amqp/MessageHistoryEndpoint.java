package io.doodler.amqp;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;

import io.doodler.common.ApiResult;

/**
 * @Description: MessageHistoryEndpoint
 * @Author: Fred Feng
 * @Date: 13/04/2023
 * @Version 1.0.0
 */
@RestControllerEndpoint(id = "amqp")
public class MessageHistoryEndpoint {

	@Autowired
	private MessageHistoryCollector messageHistoryCollector;

	@GetMapping("/history/push")
	public ApiResult<Map<String, List<Object>>> getPushHistory() {
		return ApiResult.ok(messageHistoryCollector.getPushHistory());
	}

	@GetMapping("/history/pull")
	public ApiResult<Map<String, List<Object>>> getPullHistory() {
		return ApiResult.ok(messageHistoryCollector.getPullHistory());
	}
}