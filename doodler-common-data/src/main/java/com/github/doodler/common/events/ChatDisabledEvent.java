package com.github.doodler.common.events;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: ChatDisabledEvent
 * @Author: Fred Feng
 * @Date: 10/03/2023
 * @Version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatDisabledEvent {

	private Long userId;
	private LocalDateTime expiredAt;
}