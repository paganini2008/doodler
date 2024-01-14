package com.github.doodler.common.ws.newsletter;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: Notification
 * @Author: Fred Feng
 * @Date: 28/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
public class Notification {

	private Long userId;
	private Object payload;
}