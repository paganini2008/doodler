package io.doodler.common.quartz.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Marker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import io.doodler.common.id.IdGenerator;
import io.doodler.common.jdbc.page.PageReader;
import io.doodler.common.jdbc.page.PageRequest;
import io.doodler.common.jdbc.page.PageResponse;
import io.doodler.common.quartz.executor.JobDefination;
import io.doodler.common.quartz.executor.JobSignature;
import io.doodler.common.quartz.executor.TriggerDefination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JobManager
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
@Slf4j
@Primary
@Component
public class DefaultJobManager implements JobManager, DisposableBean {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    
    @Autowired
    private Counters counters;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private Marker marker;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public Scheduler getScheduler() {
        return schedulerFactoryBean.getScheduler();
    }

    @Override
    public synchronized Date addJob(JobDefination jobDefination) throws SchedulerException {
        if (isJobExists(jobDefination.getJobName(), jobDefination.getJobGroup())) {
            String jobKey = String.format("%s.%s", jobDefination.getJobGroup(), jobDefination.getJobName());
            throw new SchedulerException("Job '" + jobKey + "' is existed");
        }
        JobDetail jobDetail = JobBuilder.newJob(DispatcherJobBean.class)
                .withIdentity(jobDefination.getJobName(), jobDefination.getJobGroup()).withDescription(
                        jobDefination.getDescription())
                .build();
        JobDataMap dataMap = jobDetail.getJobDataMap();
        long id = idGenerator.generateId();
        JobSignature jobSignature = new JobSignature(id, jobDefination.getJobGroup(), jobDefination.getJobName(),
                jobDefination.getClassName(), jobDefination.getMethod(), jobDefination.getDefaultHeaders(), applicationName,
                jobDefination.getApplicationName());
        if (StringUtils.isNotBlank(jobDefination.getUrl())) {
            jobSignature.setUrl(jobDefination.getUrl());
        }
        jobSignature.setDescription(jobDefination.getDescription());
        jobSignature.setInitialParameter(jobDefination.getInitialParameter());
        jobSignature.setMaxRetryCount(jobDefination.getMaxRetryCount());

        TriggerDefination triggerDefination = jobDefination.getTriggerDefination();
        jobSignature.setTriggerName(triggerDefination.getTriggerName());
        jobSignature.setTriggerGroup(triggerDefination.getTriggerGroup());
        dataMap.put("jobSignature", jobSignature);

        MutableTrigger trigger = (MutableTrigger) getScheduler()
                .getTrigger(TriggerKey.triggerKey(triggerDefination.getTriggerName(), triggerDefination.getTriggerGroup()));
        if (trigger == null) {
            trigger = SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionNextWithRemainingCount()
                    .withIntervalInMilliseconds(triggerDefination.getPeriod())
                    .withRepeatCount(triggerDefination.getRepeatCount() != null && triggerDefination.getRepeatCount() >= 0 ?
                            triggerDefination.getRepeatCount()
                            : SimpleTrigger.REPEAT_INDEFINITELY)
                    .build();
            trigger.setKey(TriggerKey.triggerKey(triggerDefination.getTriggerName(), triggerDefination.getTriggerGroup()));
            trigger.setStartTime(triggerDefination.getStartTime());
            if (triggerDefination.getEndTime() != null) {
                trigger.setEndTime(triggerDefination.getEndTime());
            }
            if (triggerDefination.getPriority() != null) {
                trigger.setPriority(triggerDefination.getPriority());
            }
            if (StringUtils.isNotBlank(triggerDefination.getCalendarName())) {
                trigger.setCalendarName(triggerDefination.getCalendarName());
            }
            trigger.setDescription(triggerDefination.getDescription());
        }
        Date firstFiredDate = getScheduler().scheduleJob(jobDetail, trigger);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job '{}' added and first fired date will at {}.", jobDetail.toString(), firstFiredDate);
        }
        return firstFiredDate;
    }

    @Override
    public synchronized Date addCronJob(JobDefination jobDefination) throws SchedulerException {
        if (isJobExists(jobDefination.getJobName(), jobDefination.getJobGroup())) {
            String jobKey = String.format("%s.%s", jobDefination.getJobGroup(), jobDefination.getJobName());
            throw new SchedulerException("Job '" + jobKey + "' is existed");
        }
        JobDetail jobDetail = JobBuilder.newJob(DispatcherJobBean.class)
                .withIdentity(jobDefination.getJobName(), jobDefination.getJobGroup()).withDescription(
                        jobDefination.getDescription())
                .build();
        JobDataMap dataMap = jobDetail.getJobDataMap();
        long id = idGenerator.generateId();
        JobSignature jobSignature = new JobSignature(id, jobDefination.getJobGroup(), jobDefination.getJobName(),
                jobDefination.getClassName(), jobDefination.getMethod(), jobDefination.getDefaultHeaders(), applicationName,
                jobDefination.getApplicationName());
        if (StringUtils.isNotBlank(jobDefination.getUrl())) {
            jobSignature.setUrl(jobDefination.getUrl());
        }
        jobSignature.setMaxRetryCount(jobDefination.getMaxRetryCount());
        jobSignature.setDescription(jobDefination.getDescription());
        jobSignature.setInitialParameter(jobDefination.getInitialParameter());

        TriggerDefination triggerDefination = jobDefination.getTriggerDefination();
        jobSignature.setTriggerName(triggerDefination.getTriggerName());
        jobSignature.setTriggerGroup(triggerDefination.getTriggerGroup());
        dataMap.put("jobSignature", jobSignature);

        CronTrigger trigger = (CronTrigger) getScheduler()
                .getTrigger(TriggerKey.triggerKey(triggerDefination.getTriggerName(), triggerDefination.getTriggerGroup()));
        if (trigger == null) {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(triggerDefination.getCron())
                    .withMisfireHandlingInstructionDoNothing();
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(triggerDefination.getTriggerName(), triggerDefination.getTriggerGroup())
                    .withPriority(triggerDefination.getPriority() != null ? triggerDefination.getPriority() :
                            Trigger.DEFAULT_PRIORITY)
                    .withDescription(triggerDefination.getDescription())
                    .withSchedule(scheduleBuilder);
            if (triggerDefination.getStartTime() != null) {
                triggerBuilder = triggerBuilder.startAt(triggerDefination.getStartTime());
            }
            if (triggerDefination.getEndTime() != null) {
                triggerBuilder = triggerBuilder.endAt(triggerDefination.getEndTime());
            }
            if (StringUtils.isNotBlank(triggerDefination.getCalendarName())) {
                triggerBuilder = triggerBuilder.modifiedByCalendar(triggerDefination.getCalendarName());
            }
            trigger = triggerBuilder.build();
        }
        Date firstFiredDate = getScheduler().scheduleJob(jobDetail, trigger);
        if (log.isInfoEnabled()) {
            log.info(marker, "Job '{}' added and first fired date will at {}.", jobDetail.toString(), firstFiredDate);
        }
        return firstFiredDate;
    }

    @Override
    public synchronized Date modifyJob(JobDefination jobDefination) throws SchedulerException {
        if (isJobExists(jobDefination.getJobName(), jobDefination.getJobGroup())) {
            TriggerDefination triggerDefination = jobDefination.getTriggerDefination();
            return modifyTrigger(triggerDefination.getTriggerName(),
                    triggerDefination.getTriggerGroup(),
                    triggerDefination.getStartTime(),
                    triggerDefination.getPeriod(),
                    triggerDefination.getRepeatCount() != null ? triggerDefination.getRepeatCount() : -1,
                    triggerDefination.getEndTime());
        }
        return null;
    }

    @Override
    public synchronized Date modifyCronJob(JobDefination jobDefination) throws SchedulerException {
        if (isJobExists(jobDefination.getJobName(), jobDefination.getJobGroup())) {
            TriggerDefination triggerDefination = jobDefination.getTriggerDefination();
            return modifyTrigger(triggerDefination.getTriggerName(),
                    triggerDefination.getTriggerGroup(),
                    triggerDefination.getCron(),
                    triggerDefination.getStartTime(),
                    triggerDefination.getEndTime());
        }
        return null;
    }

    @Override
    public long countOfJobs(String jobGroup, String jobNamePattern) throws SchedulerException {
        Collection<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        if (StringUtils.isNotBlank(jobNamePattern)) {
            jobKeys = jobKeys.stream().filter(key -> key.getName().contains(jobNamePattern)).collect(Collectors.toList());
        }
        return CollectionUtils.isNotEmpty(jobKeys) ? jobKeys.size() : 0;
    }

    @Override
    public long countOfTriggers(String triggerGroup, String triggerNamePattern) throws SchedulerException {
        Collection<TriggerKey> triggerKeys = getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
        if (StringUtils.isNotBlank(triggerNamePattern)) {
            triggerKeys = triggerKeys.stream().filter(key -> key.getName().contains(triggerNamePattern)).collect(
                    Collectors.toList());
        }
        return CollectionUtils.isNotEmpty(triggerKeys) ? triggerKeys.size() : 0;
    }

    @Override
    public synchronized Date modifyTrigger(String triggerName, String triggerGroup, Date startTime,
                                           long period,
                                           int repeatCount, Date endTime) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        MutableTrigger oldTrigger = (MutableTrigger) getScheduler().getTrigger(triggerKey);
        MutableTrigger trigger = SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionNextWithRemainingCount()
                .withIntervalInMilliseconds(period).withRepeatCount(
                        repeatCount >= 0 ? repeatCount : SimpleTrigger.REPEAT_INDEFINITELY)
                .build();
        trigger.setStartTime(startTime);
        if (endTime != null) {
            trigger.setEndTime(endTime);
        }
        trigger.setKey(triggerKey);
        trigger.setPriority(oldTrigger.getPriority());
        trigger.setCalendarName(oldTrigger.getCalendarName());

        Date firstFiredDate = getScheduler().rescheduleJob(triggerKey, trigger);
        if (log.isInfoEnabled()) {
            log.info(marker, "Modify trigger '{}' and first fired date will at {}", trigger, firstFiredDate);
        }
        return firstFiredDate;
    }

    @Override
    public synchronized Date modifyTrigger(String triggerName, String triggerGroup, String cron,
                                           Date startTime,
                                           Date endTime) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        CronTrigger oldTrigger = (CronTrigger) getScheduler().getTrigger(triggerKey);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        TriggerBuilder<CronTrigger> triggerBuilder = oldTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(
                scheduleBuilder);
        if (startTime != null) {
            triggerBuilder.startAt(startTime);
        }
        if (endTime != null) {
            triggerBuilder.endAt(endTime);
        }
        CronTrigger trigger = triggerBuilder.build();
        Date firstFiredDate = getScheduler().rescheduleJob(triggerKey, trigger);
        if (log.isInfoEnabled()) {
            log.info(marker, "Modify trigger '{}' and first fired date will at {}", trigger, firstFiredDate);
        }
        return firstFiredDate;
    }

    @Override
    public synchronized void pauseTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        if (getScheduler().checkExists(triggerKey)) {
            getScheduler().pauseTrigger(triggerKey);
            if (log.isInfoEnabled()) {
                log.info(marker, "Pause trigger: {}", triggerKey);
            }
        }
    }

    @Override
    public synchronized void pauseTriggers(String triggerGroup) throws Exception {
        Set<TriggerKey> triggerKeys = getScheduler().getTriggerKeys(GroupMatcher.groupEquals(triggerGroup));
        if (CollectionUtils.isNotEmpty(triggerKeys)) {
            getScheduler().pauseTriggers(GroupMatcher.groupEquals(triggerGroup));
            if (log.isInfoEnabled()) {
                log.info(marker, "Pause trigger group: {}", triggerGroup);
            }
        }
    }

    @Override
    public synchronized void pauseAll() throws SchedulerException {
        getScheduler().pauseAll();
        if (log.isWarnEnabled()) {
            log.warn(marker, "Pause all triggers.");
        }
    }

    @Override
    public synchronized void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (getScheduler().checkExists(jobKey)) {
            getScheduler().pauseJob(jobKey);
            if (log.isInfoEnabled()) {
                log.info(marker, "Pause job: {}", jobKey);
            }
        }
    }

    @Override
    public synchronized void pauseJobs(String jobGroup) throws SchedulerException {
        Set<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.groupEquals(jobGroup));
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            getScheduler().pauseJobs(GroupMatcher.groupEquals(jobGroup));
            if (log.isInfoEnabled()) {
                log.info(marker, "Pause job group: {}", jobGroup);
            }
        }
    }

    @Override
    public boolean isJobExists(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        return getScheduler().checkExists(jobKey);
    }

    @Override
    public boolean isTriggerExists(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        return getScheduler().checkExists(triggerKey);
    }

    @Override
    public synchronized boolean deleteTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        if (getScheduler().checkExists(triggerKey)) {
            getScheduler().pauseTrigger(triggerKey);
            try {
                return getScheduler().unscheduleJob(triggerKey);
            } finally {
                if (log.isInfoEnabled()) {
                    log.info(marker, "Delete trigger: {}", triggerKey);
                }
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteTriggers(String triggerGroup) throws SchedulerException {
        Set<TriggerKey> triggerKeys = getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
        if (CollectionUtils.isNotEmpty(triggerKeys)) {
            getScheduler().pauseTriggers(GroupMatcher.triggerGroupEquals(triggerGroup));
            try {
                return getScheduler().unscheduleJobs(new ArrayList<>(triggerKeys));
            } finally {
                if (log.isInfoEnabled()) {
                    log.info(marker, "Delete trigger group: {}", triggerGroup);
                }
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (getScheduler().checkExists(jobKey)) {
            try {
                return getScheduler().deleteJob(jobKey);
            } finally {
                if (log.isInfoEnabled()) {
                    log.info(marker, "Delete job: {}", jobKey);
                }
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteJobs(String jobGroup) throws SchedulerException {
        Set<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            try {
                return getScheduler().deleteJobs(new ArrayList<JobKey>(jobKeys));
            } finally {
                if (log.isInfoEnabled()) {
                    log.info(marker, "Delete job group: {}", jobGroup);
                }
            }
        }
        return false;
    }

    @Override
    public synchronized void resumeAll() throws SchedulerException {
        getScheduler().resumeAll();
        if (log.isInfoEnabled()) {
            log.info(marker, "Resume all triggers.");
        }
    }

    @Override
    public synchronized void resumeTriggers(String triggerGroup) throws Exception {
        Set<TriggerKey> triggerKeys = getScheduler().getTriggerKeys(GroupMatcher.groupEquals(triggerGroup));
        if (CollectionUtils.isNotEmpty(triggerKeys)) {
            getScheduler().resumeTriggers(GroupMatcher.groupEquals(triggerGroup));
            if (log.isInfoEnabled()) {
                log.info(marker, "Resume trigger group: {}", triggerGroup);
            }
        }
    }

    @Override
    public synchronized void resumeTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        if (getScheduler().checkExists(triggerKey)) {
            getScheduler().resumeTrigger(triggerKey);
            if (log.isInfoEnabled()) {
                log.info(marker, "Resume trigger: {}", triggerKey);
            }
        }
    }

    @Override
    public synchronized void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (getScheduler().checkExists(jobKey)) {
            getScheduler().resumeJob(jobKey);
            if (log.isInfoEnabled()) {
                log.info(marker, "Resume job: {}", jobKey);
            }
        }
    }

    @Override
    public synchronized void resumeJobs(String jobGroup) throws SchedulerException {
        Set<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            getScheduler().resumeJobs(GroupMatcher.jobGroupEquals(jobGroup));
            if (log.isInfoEnabled()) {
                log.info(marker, "Resume job group: {}", jobGroup);
            }
        }
    }

    @Override
    public synchronized void runNow(String jobName, String jobGroup, String parameter) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (getScheduler().checkExists(jobKey)) {
            if (StringUtils.isNotBlank(parameter)) {
                JobDetail jobDetail = getScheduler().getJobDetail(jobKey);
                JobSignature jobSignature = (JobSignature) jobDetail.getJobDataMap().get("jobSignature");
                jobSignature.setInitialParameter(parameter);
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("jobSignature", jobSignature);
                getScheduler().triggerJob(jobKey, jobDataMap);
            } else {
                getScheduler().triggerJob(jobKey);
            }
            if (log.isInfoEnabled()) {
                log.info(marker, "Immediately run job '{}' with params: {}", jobKey,
                        StringUtils.isNotBlank(parameter) ? parameter : "<None>");
            }
        }
    }

    @Override
    public List<TriggerDefination> queryForTrigger(String triggerGroup) throws SchedulerException {
        Set<TriggerKey> triggerKeys = getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
        if (CollectionUtils.isEmpty(triggerKeys)) {
            return Collections.emptyList();
        }
        List<TriggerDefination> definations = new ArrayList<>();
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = getScheduler().getTrigger(triggerKey);
            definations.add(convert2TriggerDefination(trigger));
        }
        return definations;
    }

    @Override
    public TriggerDefination queryForOneTrigger(String triggerName, String triggerGroup) throws Exception {
        Trigger trigger = getScheduler().getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        return convert2TriggerDefination(trigger);
    }

    private TriggerDefination convert2TriggerDefination(Trigger trigger) {
        TriggerDefination triggerDefination = new TriggerDefination();
        triggerDefination.setTriggerName(trigger.getKey().getName());
        triggerDefination.setTriggerGroup(trigger.getKey().getGroup());
        if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = ((CronTrigger) trigger);
            triggerDefination.setCron(cronTrigger.getCronExpression());
            triggerDefination.setStartTime(cronTrigger.getStartTime());
            triggerDefination.setEndTime(cronTrigger.getEndTime());
            triggerDefination.setPriority(cronTrigger.getPriority());
            triggerDefination.setCalendarName(cronTrigger.getCalendarName());
        } else if (trigger instanceof SimpleTrigger) {
            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
            triggerDefination.setDescription(simpleTrigger.getDescription());
            triggerDefination.setStartTime(simpleTrigger.getStartTime());
            triggerDefination.setEndTime(trigger.getEndTime());
            triggerDefination.setPeriod(simpleTrigger.getRepeatInterval());
            triggerDefination.setRepeatCount(simpleTrigger.getRepeatCount());
            triggerDefination.setPriority(trigger.getPriority());
            triggerDefination.setCalendarName(trigger.getCalendarName());
        }
        return triggerDefination;
    }

    @Override
    public List<JobDefination> queryForJob(String jobGroup) throws SchedulerException {
        Set<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
        if (CollectionUtils.isEmpty(jobKeys)) {
            return Collections.emptyList();
        }
        List<JobDefination> definations = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = getScheduler().getJobDetail(jobKey);
            JobDefination jobDefination = convert2JobDefination(jobDetail);
            definations.add(jobDefination);
        }
        return definations;
    }

    @Override
    public PageVo<JobDefination> pageForJob(String jobGroup, String jobNamePattern, int pageNumber, int pageSize)
            throws SchedulerException, SQLException {
        JobDefinationPageReader pageReader = new JobDefinationPageReader(jobGroup, jobNamePattern);
        PageResponse<JobDefination> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        PageVo<JobDefination> pageVo = new PageVo<>();
        pageVo.setContent(pageResponse.getContent());
        pageVo.setPageNumber(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalPages(pageResponse.getTotalPages());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        return pageVo;
    }

    @Override
    public PageVo<TriggerDefination> pageForTrigger(String triggerGroup, String triggerNamePattern, int pageNumber,
                                                    int pageSize) throws SchedulerException, SQLException {
        TriggerDefinationPageReader pageReader = new TriggerDefinationPageReader(triggerGroup, triggerNamePattern);
        PageResponse<TriggerDefination> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        PageVo<TriggerDefination> pageVo = new PageVo<>();
        pageVo.setContent(pageResponse.getContent());
        pageVo.setPageNumber(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalPages(pageResponse.getTotalPages());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        return pageVo;
    }

    @Override
    public PageVo<TriggerStatusVo> pageForTriggerStatus(String triggerGroup, String triggerNamePattern, int pageNumber,
                                                        int pageSize) throws SchedulerException, SQLException {
        TriggerStatusPageReader pageReader = new TriggerStatusPageReader(triggerGroup, triggerNamePattern);
        PageResponse<TriggerStatusVo> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        PageVo<TriggerStatusVo> pageVo = new PageVo<>();
        pageVo.setContent(pageResponse.getContent());
        pageVo.setPageNumber(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalPages(pageResponse.getTotalPages());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        return pageVo;
    }

    @Override
    public JobDefination queryForOneJob(String jobName, String jobGroup) throws SchedulerException {
        JobDetail jobDetail = getScheduler().getJobDetail(JobKey.jobKey(jobName, jobGroup));
        return convert2JobDefination(jobDetail);
    }

    private JobDefination convert2JobDefination(JobDetail jobDetail) {
        JobSignature jobSignature = (JobSignature) jobDetail.getJobDataMap().get("jobSignature");
        JobDefination jobDefination = new JobDefination();
        jobDefination.setApplicationName(jobSignature.getJobExecutor());
        jobDefination.setJobName(jobDetail.getKey().getName());
        jobDefination.setJobGroup(jobDetail.getKey().getGroup());
        jobDefination.setClassName(jobSignature.getClassName());
        jobDefination.setUrl(jobSignature.getUrl());
        jobDefination.setMethod(jobSignature.getMethod());
        jobDefination.setDefaultHeaders(jobSignature.getDefaultHeaders());
        jobDefination.setDescription(jobDetail.getDescription());
        jobDefination.setInitialParameter(jobSignature.getInitialParameter());
        jobDefination.setMaxRetryCount(jobSignature.getMaxRetryCount());
        return jobDefination;
    }

    @Override
    public List<TriggerDefination> queryForTriggerOfJob(String jobName, String jobGroup) throws SchedulerException {
        List<TriggerDefination> triggerDefinations = new ArrayList<>();
        List<? extends Trigger> triggers = getScheduler().getTriggersOfJob(JobKey.jobKey(jobName, jobGroup));
        for (Trigger trigger : triggers) {
            triggerDefinations.add(convert2TriggerDefination(trigger));
        }
        return triggerDefinations;
    }

    @Override
    public List<TriggerStatusVo> queryForTriggerStatusOfJob(String jobName, String jobGroup) throws Exception {
        List<TriggerStatusVo> triggerStatusVos = new ArrayList<>();
        List<? extends Trigger> triggers = getScheduler().getTriggersOfJob(JobKey.jobKey(jobName, jobGroup));
        for (Trigger trigger : triggers) {
            triggerStatusVos.add(convert2TriggerStatusVo(trigger));
        }
        return triggerStatusVos;
    }

    @Override
    public TriggerStatusVo queryForOneTriggerStatus(String triggerName, String triggerGroup) throws SchedulerException {
        Trigger trigger = getScheduler().getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        return convert2TriggerStatusVo(trigger);
    }

    @Override
    public List<TriggerStatusVo> queryForTriggerStatus(String triggerGroup) throws SchedulerException {
        Set<TriggerKey> triggerKeys = getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
        if (CollectionUtils.isEmpty(triggerKeys)) {
            return Collections.emptyList();
        }
        List<TriggerStatusVo> list = new ArrayList<>();
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = getScheduler().getTrigger(triggerKey);
            list.add(convert2TriggerStatusVo(trigger));
        }
        return list;
    }

    @Override
    public PageVo<JobStatusVo> pageForJobStatus(String jobGroup, String jobNamePattern, int pageNumber, int pageSize)
            throws Exception {
        JobStatusPageReader pageReader = new JobStatusPageReader(jobGroup, jobNamePattern);
        PageResponse<JobStatusVo> pageResponse = pageReader.list(PageRequest.of(pageNumber, pageSize));
        PageVo<JobStatusVo> pageVo = new PageVo<>();
        pageVo.setContent(pageResponse.getContent());
        pageVo.setPageNumber(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalPages(pageResponse.getTotalPages());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        return pageVo;
    }

    private TriggerStatusVo convert2TriggerStatusVo(Trigger trigger) throws SchedulerException {
        TriggerStatusVo vo = new TriggerStatusVo();
        vo.setCalendarName(trigger.getCalendarName());
        vo.setDescription(trigger.getDescription());
        vo.setEndTime(trigger.getEndTime());
        vo.setJobGroup(trigger.getJobKey().getGroup());
        vo.setJobName(trigger.getJobKey().getName());
        vo.setMisfireInstr(trigger.getMisfireInstruction());
        vo.setNextFireTime(trigger.getNextFireTime());
        vo.setPrevFireTime(trigger.getPreviousFireTime());
        vo.setFinalFileTime(trigger.getFinalFireTime());
        vo.setPriority(trigger.getPriority());
        vo.setStartTime(trigger.getStartTime());
        vo.setTriggerGroup(trigger.getKey().getGroup());
        vo.setTriggerName(trigger.getKey().getName());
        vo.setType(trigger.getClass().getSimpleName());
        vo.setState(getScheduler().getTriggerState(trigger.getKey()).name());
        return vo;
    }

    private JobStatusVo convert2JobStatusVo(Trigger trigger) throws SchedulerException {
        JobStatusVo vo = new JobStatusVo();
        vo.setJobGroup(trigger.getJobKey().getGroup());
        vo.setJobName(trigger.getJobKey().getName());
        vo.setTriggerGroup(trigger.getKey().getGroup());
        vo.setTriggerName(trigger.getKey().getName());
        vo.setType(trigger.getClass().getSimpleName());
        vo.setState(getScheduler().getTriggerState(trigger.getKey()).name());
        return vo;
    }

    @Override
    public List<String> getJobGroupNames() throws SchedulerException {
        return getScheduler().getJobGroupNames();
    }

    @Override
    public List<String> getTriggerGroupNames() throws SchedulerException {
        return getScheduler().getTriggerGroupNames();
    }

    @Override
    public List<JobGroupStatusVo> queryForJobGroupStatus() throws Exception {
        List<JobGroupStatusVo> list = new ArrayList<>();
        for (String jobGroup : getScheduler().getJobGroupNames()) {
            JobGroupStatusVo vo = new JobGroupStatusVo();
            vo.setJobGroup(jobGroup);
            vo.setJobCount(countOfJobs(jobGroup, null));
            Counter counter = counters.getCounter(jobGroup);
            vo.setCompletedJobCount(counter.getCount());
            vo.setRunningJobCount(counter.getRunningCount());
            vo.setErrorCount(counter.getErrorCount());
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<TriggerGroupStatusVo> queryForTriggerGroupStatus() throws SchedulerException {
        List<TriggerGroupStatusVo> list = new ArrayList<>();
        Set<String> pausedTriggerGroups = getScheduler().getPausedTriggerGroups();
        for (String triggerGroup : getScheduler().getTriggerGroupNames()) {
            TriggerGroupStatusVo vo = new TriggerGroupStatusVo();
            vo.setTriggerGroup(triggerGroup);
            vo.setTriggerCount(countOfTriggers(triggerGroup, null));
            vo.setPaused(pausedTriggerGroups.contains(triggerGroup));
            list.add(vo);
        }
        return list;
    }

    @Override
    public long getRunningJobCount(String jobGroup) throws Exception {
        List<JobExecutionContext> executingJobContextList = getScheduler().getCurrentlyExecutingJobs();
        return executingJobContextList.stream().filter(
                ctx -> ctx.getJobDetail().getKey().getGroup().equals(jobGroup)).count();
    }

    @Override
    public long getRunningJobCount() throws Exception {
        List<JobExecutionContext> executingJobContextList = getScheduler().getCurrentlyExecutingJobs();
        return executingJobContextList != null ? executingJobContextList.size() : 0;
    }

    @Override
    public JobGroupStatusVo getJobGroupStatus(String jobGroup) throws Exception {
        JobGroupStatusVo vo = new JobGroupStatusVo();
        vo.setJobGroup(jobGroup);
        vo.setJobCount(countOfJobs(jobGroup, null));
        vo.setRunningJobCount(getRunningJobCount(jobGroup));
        return vo;
    }

    @Override
    public TriggerGroupStatusVo getTriggerGroupStatus(String triggerGroup) throws Exception {
        TriggerGroupStatusVo vo = new TriggerGroupStatusVo();
        vo.setTriggerGroup(triggerGroup);
        vo.setTriggerCount(countOfTriggers(triggerGroup, null));
        Set<String> pausedTriggerGroups = getScheduler().getPausedTriggerGroups();
        vo.setPaused(pausedTriggerGroups.contains(triggerGroup));
        return vo;
    }

    @Override
    public void standby() throws SchedulerException {
        getScheduler().standby();
        if (log.isInfoEnabled()) {
            log.info(marker, "DefaultQuartzScheduler is standby.");
        }
    }

    @Override
    public void start() throws SchedulerException {
        getScheduler().start();
        if (log.isInfoEnabled()) {
            log.info(marker, "DefaultQuartzScheduler is started.");
        }
    }

    @Override
    public void stop() throws SchedulerException {
        getScheduler().shutdown(true);
        if (log.isInfoEnabled()) {
            log.info(marker, "DefaultQuartzScheduler is stopped.");
        }
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    /**
     * @Description: JobDefinationPageReader
     * @Author: Fred Feng
     * @Date: 22/10/2023
     * @Version 1.0.0
     */
    @RequiredArgsConstructor
    private class JobDefinationPageReader implements PageReader<JobDefination> {

        private final String jobGroup;
        private final String jobNamePattern;

        @Override
        public long rowCount() throws SQLException {
            try {
                return countOfJobs(jobGroup, jobNamePattern);
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
        }

        @Override
        public List<JobDefination> list(int pageNumber, int maxResults) throws SQLException {
            List<JobDefination> jobDefinations = new ArrayList<>();
            try {
                Collection<JobKey> keys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
                if (StringUtils.isNotBlank(jobNamePattern)) {
                    keys = keys.stream().filter(key -> key.getName().contains(jobNamePattern)).collect(Collectors.toList());
                }
                int fromIndex = (pageNumber - 1) * maxResults;
                int toIndex = fromIndex + maxResults;
                List<JobKey> partition = new ArrayList<>(keys);
                partition = partition.subList(fromIndex, Math.min(toIndex, keys.size()));
                for (JobKey jobKey : partition) {
                    JobDetail jobDetail = getScheduler().getJobDetail(jobKey);
                    jobDefinations.add(convert2JobDefination(jobDetail));
                }
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
            return jobDefinations;
        }
    }

    /**
     * @Description: TriggerDefinationPageReader
     * @Author: Fred Feng
     * @Date: 22/10/2023
     * @Version 1.0.0
     */
    @RequiredArgsConstructor
    private class TriggerDefinationPageReader implements PageReader<TriggerDefination> {

        private final String triggerGroup;
        private final String triggerNamePattern;

        @Override
        public long rowCount() throws SQLException {
            try {
                return countOfTriggers(triggerGroup, triggerNamePattern);
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
        }

        @Override
        public List<TriggerDefination> list(int pageNumber, int maxResults) throws SQLException {
            List<TriggerDefination> triggerStatusVos = new ArrayList<>();
            try {
                Collection<TriggerKey> keys = getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
                if (StringUtils.isNotBlank(triggerNamePattern)) {
                    keys = keys.stream().filter(key -> key.getName().contains(triggerNamePattern)).collect(
                            Collectors.toList());
                }
                int fromIndex = (pageNumber - 1) * maxResults;
                int toIndex = fromIndex + maxResults;
                List<TriggerKey> partition = new ArrayList<>(keys);
                partition = partition.subList(fromIndex, Math.min(toIndex, keys.size()));
                for (TriggerKey triggerKey : partition) {
                    Trigger trigger = getScheduler().getTrigger(triggerKey);
                    triggerStatusVos.add(convert2TriggerDefination(trigger));
                }
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
            return triggerStatusVos;
        }
    }

    /**
     * @Description: TriggerStatusPageReader
     * @Author: Fred Feng
     * @Date: 22/10/2023
     * @Version 1.0.0
     */
    @RequiredArgsConstructor
    private class TriggerStatusPageReader implements PageReader<TriggerStatusVo> {

        private final String triggerGroup;
        private final String triggerNamePattern;

        @Override
        public long rowCount() throws SQLException {
            try {
                return countOfTriggers(triggerGroup, triggerNamePattern);
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
        }

        @Override
        public List<TriggerStatusVo> list(int pageNumber, int maxResults) throws SQLException {
            List<TriggerStatusVo> triggerStatusVos = new ArrayList<>();
            try {
                Collection<TriggerKey> keys = getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
                if (StringUtils.isNotBlank(triggerNamePattern)) {
                    keys = keys.stream().filter(key -> key.getName().contains(triggerNamePattern)).collect(
                            Collectors.toList());
                }
                int fromIndex = (pageNumber - 1) * maxResults;
                int toIndex = fromIndex + maxResults;
                List<TriggerKey> partition = new ArrayList<>(keys);
                partition = partition.subList(fromIndex, Math.min(toIndex, keys.size()));
                for (TriggerKey triggerKey : partition) {
                    Trigger trigger = getScheduler().getTrigger(triggerKey);
                    triggerStatusVos.add(convert2TriggerStatusVo(trigger));
                }
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
            return triggerStatusVos;
        }
    }

    /**
     * @Description: JobStatusPageReader
     * @Author: Fred Feng
     * @Date: 22/10/2023
     * @Version 1.0.0
     */
    @RequiredArgsConstructor
    private class JobStatusPageReader implements PageReader<JobStatusVo> {

        private final String jobGroup;
        private final String jobNamePattern;

        @Override
        public long rowCount() throws SQLException {
            try {
                return countOfJobs(jobGroup, jobNamePattern);
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
        }

        @Override
        public List<JobStatusVo> list(int pageNumber, int maxResults) throws SQLException {
            List<JobStatusVo> jobStatusVos = new ArrayList<>();
            try {
                Collection<JobKey> keys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
                if (StringUtils.isNotBlank(jobNamePattern)) {
                    keys = keys.stream().filter(key -> key.getName().contains(jobNamePattern)).collect(
                            Collectors.toList());
                }
                int fromIndex = (pageNumber - 1) * maxResults;
                int toIndex = fromIndex + maxResults;
                List<JobKey> partition = new ArrayList<>(keys);
                partition = partition.subList(fromIndex, Math.min(toIndex, keys.size()));
                for (JobKey jobKey : partition) {
                    List<? extends Trigger> triggers = getScheduler().getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        jobStatusVos.add(convert2JobStatusVo(trigger));
                        break;
                    }
                }
            } catch (Exception e) {
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException(e.getMessage(), e);
            }
            return jobStatusVos;
        }
    }
}