package io.doodler.common.discovery;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import io.doodler.common.ApiResult;
import io.doodler.common.context.ApplicationContextUtils;

/**
 * @Description: DiscoveryClientEndpoint
 * @Author: Fred Feng
 * @Date: 27/03/2023
 * @Version 1.0.0
 */
@Endpoint(id = "discoveryClient")
public class DiscoveryClientEndpoint {

    @Autowired
    private DiscoveryClientService discoveryClientService;
    
    @ReadOperation
    public ApiResult<Map<String, Collection<ApplicationInfo>>> getApplicationInfos() {
        return ApiResult.ok(discoveryClientService.getApplicationInfos());
    }
    
    @ReadOperation
    public ApiResult<Collection<ApplicationInfo>> getApplicationInfos(@Selector String applicationName) {
        return ApiResult.ok(discoveryClientService.getApplicationInfos(applicationName));
    }
    
    @WriteOperation
    public ApiResult<String> forceRefresh(){
    	ApplicationContextUtils.publishEvent(new ApplicationInfoRefreshEvent(this));
    	return ApiResult.ok("Operate successfully");
    }

}