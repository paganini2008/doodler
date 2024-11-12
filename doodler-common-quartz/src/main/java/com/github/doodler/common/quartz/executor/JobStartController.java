package com.github.doodler.common.quartz.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.doodler.common.ApiResult;

/**
 * @Description: JobStartController
 * @Author: Fred Feng
 * @Date: 13/06/2023
 * @Version 1.0.0
 */
@RequestMapping("/job")
@RestController
public class JobStartController {

    @Autowired
    private JobService jobService;

    @PostMapping("/start")
    public ApiResult<String> startJob(@RequestBody RpcJobBean rpcJobBean) {
        jobService.startJob(rpcJobBean);
        return ApiResult.ok();
    }
}