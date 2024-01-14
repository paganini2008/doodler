package com.github.doodler.common.amqp;

import java.io.Serializable;

import org.springframework.amqp.core.MessageProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: DlxMessageVo
 * @Author: Fred Feng
 * @Date: 21/05/2019
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DlxMessageVo implements Serializable {

	private static final long serialVersionUID = -403743414896769136L;

	private String applicationName;
	private String eventName;
	private String body;
	private MessageProperties messageProperties;
}