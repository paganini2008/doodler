package com.github.doodler.common.quartz.executor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: JobRun
 * @Author: Fred Feng
 * @Date: 22/08/2023
 * @Version 1.0.0
 */
@ApiModel("Job Run Once")
@ToString
@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class JobRun {

	@ApiModelProperty("Job Name")
	private String jobName;

	@ApiModelProperty("Job Group Name")
	private String jobGroup;

	@ApiModelProperty("Job Initial Parameter")
	private String initialParameter;
}