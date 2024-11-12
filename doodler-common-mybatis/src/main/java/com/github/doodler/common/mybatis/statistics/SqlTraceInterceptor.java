package com.github.doodler.common.mybatis.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Marker;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: SqlTraceInterceptor
 * @Author: Fred Feng
 * @Date: 17/03/2023
 * @Version 1.0.0
 */
@Intercepts(value = {
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
@Slf4j
@RequiredArgsConstructor
public class SqlTraceInterceptor implements Interceptor {

    private static final String NEWLINE = System.getProperty("line.separator");

    private static final long DEFAULT_SLOW_SQL_THRESHOLD = 3000L;

    private final MyBatisStatisticsService statisticsService;
    
    private final Marker marker;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final long startTime = System.currentTimeMillis();
        Throwable e = null;
        try {
            return invocation.proceed();
        } catch (Throwable err) {
            e = err;
            throw err;
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;
            boolean isOk = e == null;
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            String operatorType = null;
            if (ms.getSqlCommandType() == SqlCommandType.INSERT ||
                    ms.getSqlCommandType() == SqlCommandType.UPDATE ||
                    ms.getSqlCommandType() == SqlCommandType.DELETE ||
                    ms.getSqlCommandType() == SqlCommandType.SELECT) {
                operatorType = ms.getSqlCommandType().name().toLowerCase();
            }
            if (StringUtils.isNotBlank(operatorType)) {
                statisticsService.update("sql_command", operatorType, startTime, sampler -> {
                    SqlSample sample = sampler.getSample();
                    sample.totalExecutions.increment();
                    sample.accumulatedExecutionTime.add(elapsedTime);
                    if (isOk) {
                        sample.successExecutions.increment();
                    }
                    if (elapsedTime >= DEFAULT_SLOW_SQL_THRESHOLD) {
                        sample.slowExecutions.increment();
                    }
                });
            }

            if (!isOk || elapsedTime >= DEFAULT_SLOW_SQL_THRESHOLD) {
                Object parameter = null;
                if (invocation.getArgs().length > 1) {
                    parameter = invocation.getArgs()[1];
                }
                String id = ms.getId();
                BoundSql boundSql = ms.getBoundSql(parameter);
                Configuration configuration = ms.getConfiguration();
                String actualSql = formatSql(boundSql, configuration);
                if (log.isWarnEnabled()) {
                    final String prefix = "[SQL Warning] ";
                    StringBuilder str = new StringBuilder();
                    str.append(prefix).append(NEWLINE);
                    str.append(prefix).append("Execution Signature: ").append(id).append(NEWLINE);
                    str.append(prefix).append("Execution Sql: ").append(actualSql).append(NEWLINE);
                    str.append(prefix).append("Execution Error: ").append(e != null ? e.getMessage() : "<NONE>").append(
                            NEWLINE);
                    str.append(prefix).append("Execution Time: ").append(elapsedTime).append(" (ms)");
                    log.warn(marker, str.toString());
                }
            }
        }
    }

    private String formatSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        sql = sql.replaceAll("[( )\\s\\t\\p{Zs}]+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(stringValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(stringValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(stringValue(obj)));
                    } else {
                        sql = sql.replaceFirst("\\?", "missing");
                    }
                }
            }
        }
        return sql;
    }

    private static String stringValue(Object obj) {
        String value = null;
        if (obj instanceof Number) {
            if (obj instanceof BigDecimal) {
                value = ((BigDecimal) obj).toPlainString();
            } else if (obj instanceof BigInteger) {
                value = new BigDecimal((BigInteger) obj).toPlainString();
            } else if (obj instanceof Double) {
                value = BigDecimal.valueOf((Double) obj).toPlainString();
            } else if (obj instanceof Float) {
                value = BigDecimal.valueOf((Float) obj).toPlainString();
            } else {
                value = obj.toString();
            }
        } else if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            value = "'" + DateUtil.format((Date) obj, "yyyy-MM-dd HH:mm:ss") + "'";
        } else if (obj instanceof LocalDate) {
            value = "'" + ((LocalDate) obj).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'";
        } else if (obj instanceof LocalDateTime) {
            value = "'" + ((LocalDateTime) obj).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'";
        } else if (obj != null) {
            value = obj.toString();
        } else {
            value = "NULL";
        }
        return value;
    }
}