package com.github.doodler.common.quartz.executor;

import static com.github.doodler.common.quartz.JobConstants.DEFAULT_JOB_SERVICE_NAME;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cloud.lb.LbRestTemplate;
import com.github.doodler.common.quartz.scheduler.JobOperations;
import com.github.doodler.common.quartz.scheduler.JobOperationsException;
import lombok.RequiredArgsConstructor;

/**
 * @Description: RestJobOperations
 * @Author: Fred Feng
 * @Date: 19/06/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class RestJobOperations implements JobOperations {

    private final LbRestTemplate restTemplate;
    private final HttpHeaders defaultHttpHeaders;

    @Override
    public Date addJob(JobDefination jobDefination) throws Exception {
        ResponseEntity<ApiResult<Date>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/add", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.POST, new HttpEntity<Object>(jobDefination, defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Date>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public Date addCronJob(JobDefination jobDefination) throws Exception {
        return addJob(jobDefination);
    }

    @Override
    public Date referenceJob(JobDefination jobDefination) throws Exception {
        ResponseEntity<ApiResult<Date>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/reference", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.POST, new HttpEntity<Object>(jobDefination, defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Date>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public Date referenceCronJob(JobDefination jobDefination) throws Exception {
        return referenceJob(jobDefination);
    }

    @Override
    public Date modifyJob(JobDefination jobDefination) throws Exception {
        ResponseEntity<ApiResult<Date>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/modify", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.POST, new HttpEntity<Object>(jobDefination, defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Date>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public Date modifyCronJob(JobDefination jobDefination) throws Exception {
        return modifyJob(jobDefination);
    }

    @Override
    public Date modifyTrigger(String triggerName,
                              String triggerGroup,
                              Date startTime,
                              long period,
                              int repeatCount,
                              Date endTime,
                              Map<String, Object> dataMap) throws Exception {
        TriggerDefination triggerDefination = new TriggerDefination();
        triggerDefination.setTriggerName(triggerName);
        triggerDefination.setTriggerGroup(triggerGroup);
        triggerDefination.setStartTime(startTime);
        triggerDefination.setPeriod(period);
        triggerDefination.setRepeatCount(repeatCount);
        triggerDefination.setEndTime(endTime);

        ResponseEntity<ApiResult<Date>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/modify", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.POST, new HttpEntity<Object>(triggerDefination, defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Date>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public Date modifyTrigger(String triggerName,
                              String triggerGroup,
                              String cron,
                              Date startTime,
                              Date endTime,
                              Map<String, Object> dataMap) throws Exception {
        TriggerDefination triggerDefination = new TriggerDefination();
        triggerDefination.setTriggerName(triggerName);
        triggerDefination.setTriggerGroup(triggerGroup);
        triggerDefination.setCron(cron);
        triggerDefination.setStartTime(startTime);
        triggerDefination.setEndTime(endTime);
        ResponseEntity<ApiResult<Date>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/modify", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.POST, new HttpEntity<Object>(triggerDefination, defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Date>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public void pauseTrigger(String triggerName, String triggerGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/pause/%s/%s", DEFAULT_JOB_SERVICE_NAME, triggerGroup,
                        triggerName)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void pauseTriggers(String triggerGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/pause/%s", DEFAULT_JOB_SERVICE_NAME, triggerGroup)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void pauseJob(String jobName, String jobGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/pause/%s/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup, jobName)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void pauseAll() throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/pause/all", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public boolean isJobExists(String jobName, String jobGroup) throws Exception {
        ResponseEntity<ApiResult<Boolean>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/exists/%s/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup, jobName)),
                HttpMethod.GET, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Boolean>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public boolean isTriggerExists(String triggerName, String triggerGroup) throws Exception {
        return false;
    }

    @Override
    public boolean deleteTrigger(String triggerName, String triggerGroup) throws Exception {
        ResponseEntity<ApiResult<Boolean>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/delete/%s/%s", DEFAULT_JOB_SERVICE_NAME, triggerGroup,
                        triggerName)),
                HttpMethod.DELETE, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Boolean>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public boolean deleteTriggers(String triggerGroup) throws Exception {
        ResponseEntity<ApiResult<Boolean>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/delete/%s", DEFAULT_JOB_SERVICE_NAME, triggerGroup)),
                HttpMethod.DELETE, null, new ParameterizedTypeReference<ApiResult<Boolean>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public boolean deleteJob(String jobName, String jobGroup) throws Exception {
        ResponseEntity<ApiResult<Boolean>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/delete/%s/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup, jobName)),
                HttpMethod.DELETE, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Boolean>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public boolean deleteJobs(String jobGroup) throws Exception {
        ResponseEntity<ApiResult<Boolean>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/delete/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup)),
                HttpMethod.DELETE, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<Boolean>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
        return responseEntity.getBody().getData();
    }

    @Override
    public void resumeAll() throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/resume/all", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void resumeTriggers(String triggerGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/resume/%s", DEFAULT_JOB_SERVICE_NAME, triggerGroup)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void resumeTrigger(String triggerName, String triggerGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/trigger/resume/%s/%s", DEFAULT_JOB_SERVICE_NAME, triggerGroup,
                        triggerName)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void resumeJob(String jobName, String jobGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/resume/%s/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup, jobName)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void runNow(String jobName, String jobGroup, String parameter) throws Exception {
        JobRun jobRun = new JobRun();
        jobRun.setJobName(jobName);
        jobRun.setJobGroup(jobGroup);
        jobRun.setInitialParameter(parameter);
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/run", DEFAULT_JOB_SERVICE_NAME)),
                HttpMethod.POST, new HttpEntity<Object>(jobRun, defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void pauseJobs(String jobGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/pause/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }

    @Override
    public void resumeJobs(String jobGroup) throws Exception {
        ResponseEntity<ApiResult<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("http://%s/job/man/resume/%s", DEFAULT_JOB_SERVICE_NAME, jobGroup)),
                HttpMethod.PUT, new HttpEntity<Object>(defaultHttpHeaders),
                new ParameterizedTypeReference<ApiResult<String>>() {
                });
        if (responseEntity.getStatusCode().isError()) {
            throw new JobOperationsException("Job Server Error: " + responseEntity.toString());
        }
    }
}