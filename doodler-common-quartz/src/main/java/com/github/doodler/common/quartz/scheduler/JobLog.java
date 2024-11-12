package com.github.doodler.common.quartz.scheduler;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: JobLog
 * @Author: Fred Feng
 * @Date: 15/06/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
@JsonInclude(value = Include.NON_NULL)
public class JobLog {

    public static final String NEWLINE = System.getProperty("line.separator");

    private Long id;
    private String jobGroup;
    private String jobName;
    private String triggerName;
    private String triggerGroup;
    private String className;
    private String url;
    private String method;
    private String[] headers;
    private String description;
    private String initialParameter;

    private String guid;
    private String jobScheduler;
    private String schedulerInstance;
    private String jobExecutor;
    private String executorInstance;
    private Integer status;
    private String[] errors;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private String responseBody;
    private Boolean retry;

    public String getHeaders() {
    	if(ArrayUtils.isEmpty(headers)) {
    		return null;
    	}
        StringWriter writer = new StringWriter();
        try {
            IOUtils.writeLines(Arrays.asList(headers), NEWLINE, writer);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return writer.toString();
    }

    public String getErrors() {
    	if(ArrayUtils.isEmpty(errors)) {
    		return null;
    	}
        StringWriter writer = new StringWriter();
        try {
            IOUtils.writeLines(Arrays.asList(errors), NEWLINE, writer);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return writer.toString();
    }
}