package io.doodler.mybatis.utils;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import io.doodler.common.utils.Markers;

/**
 * @Description: FixedSlf4jImpl
 * @Author: Fred Feng
 * @Date: 13/09/2023
 * @Version 1.0.0
 */
public class FixedSlf4jImpl implements Log {

	public static final String DEFAULT_LOGGER_NAME = "com.elraytech.maxibet.common.mybatis.Slf4jLogger";

    private final Logger log;
    private final Marker marker;

    public FixedSlf4jImpl(String clazz) {
        this.log = LoggerFactory.getLogger(DEFAULT_LOGGER_NAME);
        this.marker = Markers.SYSTEM;
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(marker, s, e);
        }
    }

    @Override
    public void error(String s) {
        if (log.isErrorEnabled()) {
            log.error(marker, s);
        }
    }

    @Override
    public void debug(String s) {
        if (log.isDebugEnabled()) {
            log.debug(marker, s);
        }
    }

    @Override
    public void trace(String s) {
        if (log.isTraceEnabled()) {
            log.trace(marker, s);
        }
    }

    @Override
    public void warn(String s) {
        if (log.isWarnEnabled()) {
            log.warn(marker, s);
        }
    }
}