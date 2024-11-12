package com.github.doodler.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @Description: ChatEnableEvent
 * @Author: Fred Feng
 * @Date: 27/02/2023
 * @Version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatEnabledEvent{

	private Long userId;
	
}
