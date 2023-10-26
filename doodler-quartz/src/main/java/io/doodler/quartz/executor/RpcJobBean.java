package io.doodler.quartz.executor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.doodler.discovery.ApplicationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: RpcJobBean
 * @Author: Fred Feng
 * @Date: 17/08/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value =Include.NON_NULL)
public class RpcJobBean {

	private String guid;
	private JobSignature jobSignature;
	private ApplicationInfo jobScheduler;
	private ApplicationInfo jobExecutor;
	private String[] errors;

	public RpcJobBean(String guid, JobSignature jobSignature) {
		this.guid = guid;
		this.jobSignature = jobSignature;
	}
}