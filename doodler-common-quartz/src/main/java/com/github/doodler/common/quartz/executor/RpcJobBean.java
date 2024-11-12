package com.github.doodler.common.quartz.executor;

import org.springframework.lang.Nullable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.doodler.common.cloud.ApplicationInfo;
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
@JsonInclude(value = Include.NON_NULL)
public class RpcJobBean {

    private String guid;
    private JobSignature jobSignature;
    private ApplicationInfo jobScheduler;
    private ApplicationInfo jobExecutor;
    private @Nullable String responseBody;
    private @Nullable String[] errors;
    private long startTime;

    public RpcJobBean(String guid, JobSignature jobSignature, long startTime) {
        this.guid = guid;
        this.jobSignature = jobSignature;
        this.startTime = startTime;
    }
}