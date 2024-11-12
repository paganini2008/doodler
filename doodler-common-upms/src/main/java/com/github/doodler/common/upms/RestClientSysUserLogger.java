package com.github.doodler.common.upms;

import org.springframework.scheduling.annotation.Async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: RestClientSysUserLogger
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RestClientSysUserLogger implements SysUserLogger {

    private final IRemoteSysUserLogService remoteSysUserLogService;

    @Async
    @Override
    public void saveSysUserLog(SysUserLogDto dto) {
        try {
            remoteSysUserLogService.saveSysUserLog(dto);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }
}