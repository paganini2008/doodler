package com.github.doodler.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: ChatAgreedEvent
 * @Author: Fred Feng
 * @Date: 09/03/2023
 * @Version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatAgreedEvent {

	private Long userId;
}