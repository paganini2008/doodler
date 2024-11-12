package com.github.doodler.common.upms;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.feign.RestClient;
import feign.RequestLine;

/**
 * @Description: RemoteSysUserLogService
 * @Author: Fred Feng
 * @Date: 13/02/2023
 * @Version 1.0.0
 */
@RestClient(serviceId = "crypto-upms-service")
public interface IRemoteSysUserLogService {

    @RequestLine("POST /upms/syslog/save")
    ApiResult<String> saveSysUserLog(SysUserLogDto dto);
}