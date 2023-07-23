package io.doodler.upms;

import feign.RequestLine;
import io.doodler.common.ApiResult;
import io.doodler.feign.RestClient;

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