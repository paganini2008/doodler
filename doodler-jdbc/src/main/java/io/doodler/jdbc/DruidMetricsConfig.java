package io.doodler.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: DruidMetricsConfig
 * @Author: Fred Feng
 * @Date: 14/02/2023
 * @Version 1.0.0
 */
@ConditionalOnClass({DruidDataSource.class, MeterRegistry.class})
@Configuration(proxyBeanMethods = false)
public class DruidMetricsConfig {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    public void bindMetricsRegistryToDruidDataSources(Collection<DataSource> dataSources) throws SQLException {
        List<DruidDataSource> druidDataSources = new ArrayList<>(dataSources.size());
        for (DataSource dataSource : dataSources) {
            DruidDataSource druidDataSource = dataSource.unwrap(DruidDataSource.class);
            if (druidDataSource != null) {
                druidDataSources.add(druidDataSource);
            }
        }
        DruidMetricsCollector druidCollector = new DruidMetricsCollector(druidDataSources, meterRegistry);
        druidCollector.refreshMetrics();
    }
}