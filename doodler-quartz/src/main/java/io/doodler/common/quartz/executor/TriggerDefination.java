package io.doodler.common.quartz.executor;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TriggerDefination
 * @Author: Fred Feng
 * @Date: 22/08/2023
 * @Version 1.0.0
 */
@ApiModel("Job Trigger Defination")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class TriggerDefination {

	@ApiModelProperty("Trigger Name")
	private String triggerName;

	@ApiModelProperty("Trigger Group Name")
	private String triggerGroup;

	@ApiModelProperty("Trigger Description")
	private String description;

	@ApiModelProperty("Trigger Start Time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@ApiModelProperty("Trigger Period")
	private Long period;

	@ApiModelProperty("Cron Expression")
	private String cron;

	@ApiModelProperty("Trigger Repeat Count")
	private Integer repeatCount;

	@ApiModelProperty("Trigger End Time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	@ApiModelProperty("Trigger Priority")
	private Integer priority;

	@ApiModelProperty("Calendar Name")
	private String calendarName;
}