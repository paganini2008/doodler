package com.github.doodler.common.upms;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: NoopSysUserLogger
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@Slf4j
public class NoOpSysUserLogger implements SysUserLogger {

    @Override
    public void saveSysUserLog(SysUserLogDto dto) {
        if (log.isInfoEnabled()) {
            log.info(dto.toString());
        }
    }
}