package com.github.doodler.common.upms;

import java.util.List;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.feign.RestClient;
import feign.RequestLine;

/**
 * 
 * @Description: IRemoteSysPermissionService
 * @Author: Fred Feng
 * @Date: 07/11/2023
 * @Version 1.0.0
 */
@RestClient(serviceId = "crypto-upms-service")
public interface IRemoteSysPermissionService {

	@RequestLine("POST /upms/permission/external/save")
    ApiResult<String> saveExternalPermissions(List<PermissionPersistenceDto> dtos);
	
}
