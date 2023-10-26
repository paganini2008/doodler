package io.doodler.quartz.executor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: JobDefination
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@ApiModel("Job Defination")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class JobDefination {

    @ApiModelProperty("applicationName")
    private String applicationName;

    @ApiModelProperty("Job Name")
    private String jobName;

    @ApiModelProperty("Job Group Name")
    private String jobGroup;

    @ApiModelProperty("Job Description")
    private String description;

    @ApiModelProperty("Job Execution Class Name")
    private String className;

    @ApiModelProperty("Job Execution URL")
    private String url;

    @ApiModelProperty("Job Execution Method")
    private String method;

    @ApiModelProperty("Http headers for running job")
    private String[] headers;

    @ApiModelProperty("Job Execution Initial Parameter")
    private String initialParameter;
    
    @ApiModelProperty("Job Retry Maximum Retry Count")
    private Integer maxRetryCount;

    @ApiModelProperty("Job Trigger Defination")
    private TriggerDefination triggerDefination;
}