package com.github.doodler.common.events;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: ApplicationExceptionEvent
 * @Author: Fred Feng
 * @Date: 08/02/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Application exception event")
public class ApplicationExceptionEvent {

    private String applicationName;
    private String exceptionClassName;
    private int errorCode;
    private String errorMessage;
    private String[] errorDetails;
}