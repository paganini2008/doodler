package com.github.doodler.common.ws.client;

import java.util.UUID;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: WsKey
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@Data
public class WsKey {

	private String url;
	private String token;

	public WsKey(String url, String token) {
		this.url = url;
		this.token = StringUtils.isNotBlank(token) ? token : UUID.randomUUID().toString();
	}
}