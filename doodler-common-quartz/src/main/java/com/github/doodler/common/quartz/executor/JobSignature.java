package com.github.doodler.common.quartz.executor;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: JobSignature
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class JobSignature implements Serializable {

    private static final long serialVersionUID = -4322113989579884638L;

    private Long id;
    private String jobGroup;
    private String jobName;
    private String triggerName;
    private String triggerGroup;
    private String className;
    private String url;
    private String method;
    private String[] defaultHeaders;
    private String jobScheduler;
    private String jobExecutor;
    private String description;
    private String initialParameter;
    private Integer maxRetryCount;

    public JobSignature(Long id, String jobGroup, 
    		            String jobName, String className, 
    		            String method, String[] headers,
                        String jobScheduler, String jobExecutor,
                        String description, String initialParameter) {
        this(id, jobGroup, jobName, className, method, headers, jobScheduler, jobExecutor);
        this.description = description;
        this.initialParameter = initialParameter;
    }

    public JobSignature(Long id, String jobGroup, 
    		            String jobName, String className, 
    		            String method, String[] defaultHeaders,
                        String jobScheduler, String jobExecutor) {
        this.id = id;
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.className = className;
        this.method = method;
        this.defaultHeaders = defaultHeaders;
        this.jobScheduler = jobScheduler;
        this.jobExecutor = jobExecutor;
    }

    public String toString() {
        String repr;
        if (StringUtils.isNotBlank(url)) {
            repr = method.toUpperCase() + " " + url;
        } else {
            repr = className + "#" + method;
        }
        return String.format("%s.%s [%s]", jobGroup, jobName, repr);
    }
}