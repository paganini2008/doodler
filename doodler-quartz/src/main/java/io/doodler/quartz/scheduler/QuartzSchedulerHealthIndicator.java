package io.doodler.quartz.scheduler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @Description: QuartzSchedulerHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class QuartzSchedulerHealthIndicator extends AbstractHealthIndicator implements InitializingBean {

    private final DataSource dataSource;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private final LongAdder activeTaskCounter = new LongAdder();
    private final LongAdder completedTaskCounter = new LongAdder();
    private final LongAdder errorCounter = new LongAdder();

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        builder.up();

        String sql = "select status, count(*) as count from qrtz_job_run_log where retry=:retry group by status";
        List<Summary> summaries = jdbcTemplate.query(sql, Collections.singletonMap("retry", false),
                new BeanPropertyRowMapper<Summary>(Summary.class));
        if (CollectionUtils.isNotEmpty(summaries)) {
            for (Summary summary : summaries) {
                if (summary.getStatus() != null) {
                    switch (summary.getStatus()) {
                        case 0:
                            errorCounter.add(summary.getCount());
                            break;
                        case 1:
                            completedTaskCounter.add(summary.getCount());
                            break;
                        default:
                            break;
                    }
                } else {
                    activeTaskCounter.add(summary.getCount());
                }
            }
        }
        builder.withDetail("activeTaskCount", getActiveTaskCount())
               .withDetail("completedTaskCount", getCompletedTaskCount())
               .withDetail("errorCount", getErrorCount());
        builder.build();
    }

    public long getActiveTaskCount() {
        return activeTaskCounter.longValue();
    }

    public long getCompletedTaskCount() {
        return completedTaskCounter.longValue();
    }

    public long getErrorCount() {
        return errorCounter.longValue();
    }

    @Getter
    @Setter
    private static class Summary {

        private Integer status;
        private long count;
    }
}