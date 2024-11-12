package com.github.doodler.common.id;

import java.util.List;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.feign.RestClient;
import feign.Param;
import feign.RequestLine;

/**
 * 
 * @Description: RemoteIdService
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@RestClient(serviceId = "doodler-common-service")
public interface RemoteIdService {

    @RequestLine("GET /common/id/long/create/{n}")
    ApiResult<List<Long>> createManyLongs(@Param("n") int n);

    @RequestLine("GET /common/id/string/create/{n}")
    ApiResult<List<String>> createManyStrings(@Param("n") int n);

}
