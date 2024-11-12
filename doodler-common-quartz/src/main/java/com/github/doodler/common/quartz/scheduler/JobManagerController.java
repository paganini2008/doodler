package com.github.doodler.common.quartz.scheduler;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.quartz.executor.JobDefination;
import com.github.doodler.common.quartz.executor.JobRun;
import com.github.doodler.common.quartz.executor.TriggerDefination;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: JobManagerController
 * @Author: Fred Feng
 * @Date: 14/06/2023
 * @Version 1.0.0
 */
@RequestMapping("/job/man")
@RestController
public class JobManagerController {

    @Autowired
    private JobManager jobOperations;

    @PostMapping("/add")
    public ApiResult<Date> addJob(@RequestBody JobDefination jobDefination) throws Exception {
        Date date;
        if (jobDefination.getTriggerDefination() != null &&
                StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
            date = jobOperations.addCronJob(jobDefination);
        } else {
            date = jobOperations.addJob(jobDefination);
        }
        return ApiResult.ok(date);
    }

    @PostMapping("/reference")
    public ApiResult<Date> referenceJob(@RequestBody JobDefination jobDefination) throws Exception {
        Date date;
        if (jobDefination.getTriggerDefination() != null &&
                StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
            date = jobOperations.referenceCronJob(jobDefination);
        } else {
            date = jobOperations.referenceJob(jobDefination);
        }
        return ApiResult.ok(date);
    }

    @PostMapping("/modify")
    public ApiResult<Date> modifyJob(@RequestBody JobDefination jobDefination) throws Exception {
        Date date;
        if (StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
            date = jobOperations.modifyCronJob(jobDefination);
        } else {
            date = jobOperations.modifyJob(jobDefination);
        }
        return ApiResult.ok(date);
    }

    @GetMapping("/exists/{jobGroup}/{jobName}")
    public ApiResult<Boolean> isJobExists(@PathVariable("jobGroup") String jobGroup,
                                          @PathVariable("jobName") String jobName) throws Exception {
        boolean result = jobOperations.isJobExists(jobName, jobGroup);
        return ApiResult.ok(result);
    }

    @PutMapping("/pause/{jobGroup}/{jobName}")
    public ApiResult<String> pauseJob(
            @PathVariable("jobGroup") String jobGroup,
            @PathVariable("jobName") String jobName) throws Exception {
        jobOperations.pauseJob(jobName, jobGroup);
        return ApiResult.ok();
    }

    @PutMapping("/pause/{jobGroup}")
    public ApiResult<String> pauseJobs(@PathVariable("jobGroup") String jobGroup) throws Exception {
        jobOperations.pauseJobs(jobGroup);
        return ApiResult.ok();
    }

    @PutMapping("/resume/{jobGroup}/{jobName}")
    public ApiResult<String> resumeJob(@PathVariable("jobGroup") String jobGroup,
                                       @PathVariable("jobName") String jobName) throws Exception {
        jobOperations.resumeJob(jobName, jobGroup);
        return ApiResult.ok();
    }

    @PutMapping("/resume/{jobGroup}")
    public ApiResult<String> resumeJobs(@PathVariable("jobGroup") String jobGroup) throws Exception {
        jobOperations.resumeJobs(jobGroup);
        return ApiResult.ok();
    }

    @PutMapping("/trigger/pause/{triggerGroup}/{triggerName}")
    public ApiResult<String> pauseTrigger(
            @PathVariable("triggerGroup") String triggerGroup,
            @PathVariable("triggerName") String triggerName
    ) throws Exception {
        jobOperations.pauseTrigger(triggerName, triggerGroup);
        return ApiResult.ok();
    }

    @PutMapping("/trigger/pause/{triggerGroup}")
    public ApiResult<String> pauseTriggers(
            @PathVariable("triggerGroup") String triggerGroup) throws Exception {
        jobOperations.pauseTriggers(triggerGroup);
        return ApiResult.ok();
    }

    @PutMapping("/trigger/resume/{triggerGroup}/{triggerName}")
    public ApiResult<String> resumeTrigger(
            @PathVariable("triggerGroup") String triggerGroup,
            @PathVariable("triggerName") String triggerName) throws Exception {
        jobOperations.resumeTrigger(triggerName, triggerGroup);
        return ApiResult.ok();
    }

    @PutMapping("/trigger/resume/{triggerGroup}")
    public ApiResult<String> resumeTriggers(
            @PathVariable("triggerGroup") String triggerGroup) throws Exception {
        jobOperations.resumeTriggers(triggerGroup);
        return ApiResult.ok();
    }

    @PutMapping("/trigger/pause/all")
    public ApiResult<String> pauseAll() throws Exception {
        jobOperations.pauseAll();
        return ApiResult.ok();
    }

    @PutMapping("/trigger/resume/all")
    public ApiResult<String> resumeAll() throws Exception {
        jobOperations.resumeAll();
        return ApiResult.ok();
    }

    @PostMapping("/trigger/modify")
    public ApiResult<Date> modifyTrigger(
            @RequestBody TriggerDefination triggerDefination) throws Exception {
        Date date;
        if (StringUtils.isNotBlank(triggerDefination.getCron())) {
            date = jobOperations.modifyTrigger(triggerDefination.getTriggerName(),
                    triggerDefination.getTriggerGroup(),
                    triggerDefination.getCron(),
                    triggerDefination.getStartTime(),
                    triggerDefination.getEndTime(),
                    null);
        } else {
            date = jobOperations.modifyTrigger(triggerDefination.getTriggerName(),
                    triggerDefination.getTriggerGroup(),
                    triggerDefination.getStartTime(),
                    triggerDefination.getPeriod(),
                    triggerDefination.getRepeatCount(),
                    triggerDefination.getEndTime(),
                    null);
        }
        return ApiResult.ok(date);
    }

    @DeleteMapping("/trigger/delete/{triggerGroup}/{triggerName}")
    public ApiResult<Boolean> deleteTrigger(@PathVariable("triggerGroup") String triggerGroup,
                                            @PathVariable("triggerName") String triggerName) throws Exception {
        boolean result = jobOperations.deleteTrigger(triggerName, triggerGroup);
        return ApiResult.ok(result);
    }

    @DeleteMapping("/trigger/delete/{triggerGroup}")
    public ApiResult<Boolean> deleteTriggers(@PathVariable("triggerGroup") String triggerGroup) throws Exception {
        boolean result = jobOperations.deleteTriggers(triggerGroup);
        return ApiResult.ok(result);
    }

    @DeleteMapping("/delete/{jobGroup}/{jobName}")
    public ApiResult<Boolean> deleteJob(@PathVariable("jobGroup") String jobGroup,
                                        @PathVariable("jobName") String jobName) throws Exception {
        boolean result = jobOperations.deleteJob(jobName, jobGroup);
        return ApiResult.ok(result);
    }

    @DeleteMapping("/delete/{jobGroup}")
    public ApiResult<Boolean> deleteJobs(@PathVariable("jobGroup") String jobGroup) throws Exception {
        boolean result = jobOperations.deleteJobs(jobGroup);
        return ApiResult.ok(result);
    }

    @PostMapping("/run")
    public ApiResult<String> runNow(@RequestBody JobRun jobRun) throws Exception {
        jobOperations.runNow(jobRun.getJobName(), jobRun.getJobGroup(), jobRun.getInitialParameter());
        return ApiResult.ok();
    }

    @PostMapping("/maintain/{status}")
    public ApiResult<String> maintain(@PathVariable("status") int status)
            throws Exception {
        switch (status) {
            case 0:
                jobOperations.standby();
                break;
            case 1:
                jobOperations.start();
                break;
            case 2:
                jobOperations.stop();
                break;
            default:
                throw new UnsupportedOperationException("Unknown status: " + status);
        }
        return ApiResult.ok();
    }

    @GetMapping("/group/status")
    public ApiResult<JobGroupStatusVo> getJobGroupStatus(@RequestParam("jobGroup") String jobGroup) throws Exception {
        return ApiResult.ok(jobOperations.getJobGroupStatus(jobGroup));
    }

    @GetMapping("/trigger/group/status")
    public ApiResult<TriggerGroupStatusVo> getTriggerGroupStatus(@RequestParam("triggerGroup") String triggerGroup)
            throws Exception {
        return ApiResult.ok(jobOperations.getTriggerGroupStatus(triggerGroup));
    }
}