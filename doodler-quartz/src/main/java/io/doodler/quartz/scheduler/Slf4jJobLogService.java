package io.doodler.quartz.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;

/**
 * @Description: Slf4jJobLogService
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class Slf4jJobLogService extends JobLogService {

    private final Marker marker;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    protected void writeLog(JobLog jobLog) {
        if (jobLog != null) {
            String msg = objectMapper.writeValueAsString(jobLog);
            if (log.isInfoEnabled()) {
                log.info(marker, msg);
            }
        }
    }

    @Override
    public PageVo<JobLog> readLog(JobLogQuery logQuery) {
        return new PageVo<>();
    }
}