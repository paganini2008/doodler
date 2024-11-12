package com.github.doodler.common.jdbc.druid;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.RequiredArgsConstructor;

/**
 * @Description: DruidHealthIndicator
 * @Author: Fred Feng
 * @Date: 15/10/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DruidHealthIndicator extends AbstractHealthIndicator {

    private final DataSource dataSource;

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        DruidDataSource druidDataSource = dataSource.unwrap(DruidDataSource.class);

        Map<String, Object> infoMap = new LinkedHashMap<>();
        // basic configurations
        infoMap.put("Initial size", druidDataSource.getInitialSize());
        infoMap.put("Min idle", druidDataSource.getMinIdle());
        infoMap.put("Max active", druidDataSource.getMaxActive());

        // connection pool core metrics
        infoMap.put("Active count", druidDataSource.getActiveCount());
        infoMap.put("Active peak", druidDataSource.getActivePeak());
        infoMap.put("Pooling peak", druidDataSource.getPoolingPeak());
        infoMap.put("Pooling count", druidDataSource.getPoolingCount());
        infoMap.put("Wait thread count", druidDataSource.getWaitThreadCount());

        // connection pool detail metrics
        infoMap.put("Not empty wait count", druidDataSource.getNotEmptyWaitCount());
        infoMap.put("Not empty wait millis", druidDataSource.getNotEmptyWaitMillis());
        infoMap.put("Not empty thread count", druidDataSource.getNotEmptyWaitThreadCount());

        infoMap.put("Logic connect count", druidDataSource.getConnectCount());
        infoMap.put("Logic close count", druidDataSource.getCloseCount());
        infoMap.put("Logic connect error count", druidDataSource.getConnectErrorCount());
        infoMap.put("Physical connect count", druidDataSource.getCreateCount());
        infoMap.put("Physical close count", druidDataSource.getDestroyCount());
        infoMap.put("Physical connect error count", druidDataSource.getCreateErrorCount());

        // sql execution core metrics
        infoMap.put("Error count", druidDataSource.getErrorCount());
        infoMap.put("Execute count", druidDataSource.getExecuteCount());
        // transaction metrics
        infoMap.put("Start transaction count", druidDataSource.getStartTransactionCount());
        infoMap.put("Commit count", druidDataSource.getCommitCount());
        infoMap.put("Rollback count", druidDataSource.getRollbackCount());

        // sql execution detail
        infoMap.put("Prepared statement open count", druidDataSource.getPreparedStatementCount());
        infoMap.put("Prepared statement closed count", druidDataSource.getClosedPreparedStatementCount());
        infoMap.put("PS cache access count", druidDataSource.getCachedPreparedStatementAccessCount());
        infoMap.put("PS cache hit count", druidDataSource.getCachedPreparedStatementHitCount());
        infoMap.put("PS cache miss count", druidDataSource.getCachedPreparedStatementMissCount());
        infoMap.put("Execute query count", druidDataSource.getExecuteQueryCount());
        infoMap.put("Execute update count", druidDataSource.getExecuteUpdateCount());
        infoMap.put("Execute batch count", druidDataSource.getExecuteBatchCount());

        // none core metrics, some are static configurations
        infoMap.put("Max wait", druidDataSource.getMaxWait());
        infoMap.put("Max wait thread count", druidDataSource.getMaxWaitThreadCount());
        infoMap.put("Login timeout", druidDataSource.getLoginTimeout());
        infoMap.put("Query timeout", druidDataSource.getQueryTimeout());
        infoMap.put("Transaction query timeout", druidDataSource.getTransactionQueryTimeout());
        
        builder.up().withDetails(infoMap);
        
        builder.build();
    }
}