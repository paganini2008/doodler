package io.doodler.quartz.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.doodler.common.ApiResult;
import io.doodler.quartz.executor.RpcJobBean;

/**
 * @Description: JobEndController
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
@RequestMapping("/job")
@RestController
public class JobEndController {

    @Autowired()
    private JobLogService jobLogService;

    @PostMapping("/end")
    public ApiResult<String> endJob(@RequestBody RpcJobBean rpcJobBean) {
        jobLogService.endJob(rpcJobBean.getGuid(), rpcJobBean.getJobSignature(), rpcJobBean.getJobExecutor(), rpcJobBean.getErrors(), false);
        return ApiResult.ok();
    }
}